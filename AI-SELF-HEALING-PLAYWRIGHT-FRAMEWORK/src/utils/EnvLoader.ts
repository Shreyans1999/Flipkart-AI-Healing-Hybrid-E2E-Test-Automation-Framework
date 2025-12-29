import * as dotenv from 'dotenv';
import * as path from 'path';
import { devConfig } from '../config/env.dev.js';
import { qaConfig } from '../config/env.qa.js';
import { prodConfig } from '../config/env.prod.js';

// Load .env file
dotenv.config({ path: path.join(process.cwd(), '.env') });

/**
 * Environment types supported by the framework
 */
export type Environment = 'dev' | 'qa' | 'prod';

/**
 * LLM Provider types (OpenAI or Ollama for local testing)
 */
export type LLMProvider = 'openai' | 'ollama';

/**
 * Environment configuration interface
 */
export interface EnvConfig {
    baseUrl: string;
    timeout: number;
    retries: number;
    headless: boolean;
    slowMo: number;
    apiBaseUrl: string;
    testUser: {
        username: string;
        password: string;
    };
    logLevel: string;
    healing: {
        enabled: boolean;
        maxRetries: number;
        confidenceThreshold: number;
        autoUpdateLocators: boolean;
    };
}

/**
 * Get environment configuration based on TEST_ENV
 */
export function getEnvConfig(): EnvConfig {
    const env = (process.env.TEST_ENV || 'dev') as Environment;

    switch (env) {
        case 'qa':
            return qaConfig;
        case 'prod':
            return prodConfig;
        case 'dev':
        default:
            return devConfig;
    }
}

/**
 * Environment variable loader with validation
 */
export class EnvLoader {
    private static instance: EnvLoader;
    private static warningShown: boolean = false;
    private envConfig: EnvConfig;

    private constructor() {
        this.envConfig = getEnvConfig();
        this.validateRequiredEnvVars();
    }

    /**
     * Singleton instance
     */
    public static getInstance(): EnvLoader {
        if (!EnvLoader.instance) {
            EnvLoader.instance = new EnvLoader();
        }
        return EnvLoader.instance;
    }

    /**
     * Validate required environment variables
     */
    private validateRequiredEnvVars(): void {
        const provider = this.getLLMProvider();

        // Only show warning once per process and only if healing logging is enabled
        if (provider === 'openai' && !process.env.OPENAI_API_KEY && !EnvLoader.warningShown) {
            if (process.env.LOG_AI_HEALING !== 'false') {
                console.warn('⚠️ OPENAI_API_KEY not set. AI healing will use fallback selectors only.');
            }
            EnvLoader.warningShown = true;
        }
    }

    /**
     * Get current environment
     */
    public getEnvironment(): Environment {
        return (process.env.TEST_ENV || 'dev') as Environment;
    }

    /**
     * Get LLM provider
     */
    public getLLMProvider(): LLMProvider {
        return (process.env.LLM_PROVIDER || 'openai') as LLMProvider;
    }

    /**
     * Get base URL for tests
     */
    public getBaseUrl(): string {
        return process.env.BASE_URL || this.envConfig.baseUrl;
    }

    /**
     * Get OpenAI configuration
     */
    public getOpenAIConfig(): { apiKey: string; model: string } {
        return {
            apiKey: process.env.OPENAI_API_KEY || '',
            model: process.env.OPENAI_MODEL || 'gpt-4-turbo-preview',
        };
    }

    /**
     * Get Ollama configuration (for local LLM testing)
     */
    public getOllamaConfig(): { baseUrl: string; model: string } {
        return {
            baseUrl: process.env.OLLAMA_BASE_URL || 'http://localhost:11434',
            model: process.env.OLLAMA_MODEL || 'llama2',
        };
    }

    /**
     * Get healing configuration
     */
    public getHealingConfig(): EnvConfig['healing'] {
        return {
            enabled: process.env.HEALING_ENABLED !== 'false' && this.envConfig.healing.enabled,
            maxRetries: parseInt(process.env.MAX_HEALING_RETRIES || '') || this.envConfig.healing.maxRetries,
            confidenceThreshold: parseFloat(process.env.HEALING_CONFIDENCE_THRESHOLD || '') || this.envConfig.healing.confidenceThreshold,
            autoUpdateLocators: process.env.AUTO_UPDATE_LOCATORS !== 'false' && this.envConfig.healing.autoUpdateLocators,
        };
    }

    /**
     * Get full environment config
     */
    public getConfig(): EnvConfig {
        return this.envConfig;
    }

    /**
     * Check if AI healing logging is enabled
     */
    public isHealingLoggingEnabled(): boolean {
        return process.env.LOG_AI_HEALING !== 'false';
    }
}

// Export singleton instance
export const envLoader = EnvLoader.getInstance();
export default envLoader;
