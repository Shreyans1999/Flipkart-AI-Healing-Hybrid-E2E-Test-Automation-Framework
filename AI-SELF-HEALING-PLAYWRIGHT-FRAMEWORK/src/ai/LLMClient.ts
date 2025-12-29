import OpenAI from 'openai';
import axios from 'axios';
import { envLoader, LLMProvider } from '../utils/EnvLoader.js';
import { logger } from '../utils/Logger.js';

/**
 * LLM Response structure for selector generation
 */
export interface LLMSelectorResponse {
    selectors: string[];
    reasoning: string;
    confidence: number;
}

/**
 * Abstract LLM Client interface
 */
export interface ILLMClient {
    generateSelectors(prompt: string): Promise<LLMSelectorResponse>;
}

/**
 * OpenAI Client implementation (Primary)
 */
export class OpenAIClient implements ILLMClient {
    private client: OpenAI;
    private model: string;

    constructor() {
        const config = envLoader.getOpenAIConfig();
        this.client = new OpenAI({
            apiKey: config.apiKey,
        });
        this.model = config.model;
        logger.info(`OpenAI Client initialized with model: ${this.model}`);
    }

    async generateSelectors(prompt: string): Promise<LLMSelectorResponse> {
        try {
            const response = await this.client.chat.completions.create({
                model: this.model,
                messages: [
                    {
                        role: 'system',
                        content: this.getSystemPrompt(),
                    },
                    {
                        role: 'user',
                        content: prompt,
                    },
                ],
                temperature: 0.3,
                max_tokens: 1000,
                response_format: { type: 'json_object' },
            });

            const content = response.choices[0]?.message?.content;
            if (!content) {
                throw new Error('Empty response from OpenAI');
            }

            return this.parseResponse(content);
        } catch (error) {
            logger.error('OpenAI API error', { error });
            throw error;
        }
    }

    private getSystemPrompt(): string {
        return `You are an expert in web automation and Playwright testing.
Your task is to generate alternative CSS selectors or Playwright locator strings for elements that couldn't be found.

RULES:
1. Generate 3-5 alternative selectors
2. Prefer accessibility selectors (role, aria-label, text content)
3. Prefer stable attributes (data-testid, data-*, name, id)
4. Avoid fragile XPath with position indexes
5. All selectors must be valid Playwright locator syntax
6. Consider the element's purpose and context

VALID PLAYWRIGHT SELECTOR FORMATS:
- CSS: "#id", ".class", "tag", "[attribute='value']"
- Text: "text=Login", "button:has-text('Submit')"
- Role: "role=button[name='Login']"
- Playwright: "button >> text=Login"

Respond ONLY with valid JSON in this exact format:
{
  "selectors": ["selector1", "selector2", "selector3"],
  "reasoning": "Brief explanation of why these selectors should work",
  "confidence": 0.8
}`;
    }

    private parseResponse(content: string): LLMSelectorResponse {
        try {
            const parsed = JSON.parse(content);
            return {
                selectors: parsed.selectors || [],
                reasoning: parsed.reasoning || '',
                confidence: parsed.confidence || 0.5,
            };
        } catch (error) {
            logger.error('Failed to parse OpenAI response', { content, error });
            return {
                selectors: [],
                reasoning: 'Failed to parse response',
                confidence: 0,
            };
        }
    }
}

/**
 * Ollama (Local LLM) Client implementation
 * Use this for free local testing without API costs
 */
export class OllamaClient implements ILLMClient {
    private baseUrl: string;
    private model: string;

    constructor() {
        const config = envLoader.getOllamaConfig();
        this.baseUrl = config.baseUrl;
        this.model = config.model;
        logger.info(`Ollama Client initialized with model: ${this.model}`);
    }

    async generateSelectors(prompt: string): Promise<LLMSelectorResponse> {
        try {
            const response = await axios.post(
                `${this.baseUrl}/api/generate`,
                {
                    model: this.model,
                    prompt: `${this.getSystemPrompt()}\n\nUser Query:\n${prompt}`,
                    stream: false,
                    format: 'json',
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    timeout: 60000, // Local LLMs can be slow
                }
            );

            const content = response.data.response;
            if (!content) {
                throw new Error('Empty response from Ollama');
            }

            return this.parseResponse(content);
        } catch (error) {
            logger.error('Ollama API error', { error });
            throw error;
        }
    }

    private getSystemPrompt(): string {
        return `You are an expert in web automation and Playwright testing.
Generate 3-5 alternative CSS selectors for a failed element.

RULES:
1. Prefer accessibility selectors
2. Prefer stable attributes (data-testid, id, name)
3. Use valid Playwright selector syntax
4. Respond with JSON only

Response format:
{"selectors": ["sel1", "sel2", "sel3"], "reasoning": "explanation", "confidence": 0.8}`;
    }

    private parseResponse(content: string): LLMSelectorResponse {
        try {
            // Try to extract JSON from response
            const jsonMatch = content.match(/\{[\s\S]*\}/);
            if (jsonMatch) {
                const parsed = JSON.parse(jsonMatch[0]);
                return {
                    selectors: parsed.selectors || [],
                    reasoning: parsed.reasoning || '',
                    confidence: parsed.confidence || 0.5,
                };
            }
            throw new Error('No JSON found in response');
        } catch (error) {
            logger.error('Failed to parse Ollama response', { content, error });
            return {
                selectors: [],
                reasoning: 'Failed to parse response',
                confidence: 0,
            };
        }
    }
}

/**
 * LLM Client Factory
 * Creates the appropriate LLM client based on configuration
 */
export class LLMClientFactory {
    private static instance: ILLMClient | null = null;

    public static getClient(provider?: LLMProvider): ILLMClient {
        if (LLMClientFactory.instance) {
            return LLMClientFactory.instance;
        }

        const selectedProvider = provider || envLoader.getLLMProvider();

        switch (selectedProvider) {
            case 'ollama':
                LLMClientFactory.instance = new OllamaClient();
                break;
            case 'openai':
            default:
                LLMClientFactory.instance = new OpenAIClient();
                break;
        }

        logger.info(`LLM Client initialized: ${selectedProvider}`);
        return LLMClientFactory.instance;
    }

    public static resetClient(): void {
        LLMClientFactory.instance = null;
    }
}

export default LLMClientFactory;
