import * as winston from 'winston';
import * as path from 'path';
import * as fs from 'fs';

// Ensure logs directory exists
const logsDir = path.join(process.cwd(), 'logs');
if (!fs.existsSync(logsDir)) {
    fs.mkdirSync(logsDir, { recursive: true });
}

/**
 * Custom format for structured JSON logging
 */
const structuredFormat = winston.format.combine(
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss.SSS' }),
    winston.format.errors({ stack: true }),
    winston.format.json()
);

/**
 * Console format with colors for development
 */
const consoleFormat = winston.format.combine(
    winston.format.timestamp({ format: 'HH:mm:ss' }),
    winston.format.colorize(),
    winston.format.printf(({ level, message, timestamp, ...meta }) => {
        const metaStr = Object.keys(meta).length ? ` ${JSON.stringify(meta)}` : '';
        return `[${timestamp}] ${level}: ${message}${metaStr}`;
    })
);

/**
 * Main application logger
 */
export const logger = winston.createLogger({
    level: process.env.LOG_LEVEL || 'info',
    format: structuredFormat,
    defaultMeta: { service: 'ai-self-healing-playwright' },
    transports: [
        // Write all logs to combined.log
        new winston.transports.File({
            filename: path.join(logsDir, 'combined.log'),
            maxsize: 5242880, // 5MB
            maxFiles: 5,
        }),
        // Write error logs to error.log
        new winston.transports.File({
            filename: path.join(logsDir, 'error.log'),
            level: 'error',
            maxsize: 5242880,
            maxFiles: 5,
        }),
    ],
});

// Add console transport for non-production
if (process.env.NODE_ENV !== 'production') {
    logger.add(
        new winston.transports.Console({
            format: consoleFormat,
        })
    );
}

/**
 * Dedicated AI Healing Logger
 * Logs all self-healing events with detailed information
 */
export const healingLogger = winston.createLogger({
    level: 'info',
    format: structuredFormat,
    defaultMeta: { service: 'ai-healing-engine' },
    transports: [
        new winston.transports.File({
            filename: path.join(logsDir, 'healing.log'),
            maxsize: 5242880,
            maxFiles: 10,
        }),
        new winston.transports.Console({
            format: winston.format.combine(
                winston.format.timestamp({ format: 'HH:mm:ss' }),
                winston.format.colorize(),
                winston.format.printf(({ level, message, timestamp, ...meta }) => {
                    return `[${timestamp}] 🔧 ${level}: ${message}`;
                })
            ),
        }),
    ],
});

/**
 * Log healing event with structured data
 */
export interface HealingLogData {
    testName: string;
    elementKey: string;
    originalSelector: string;
    healedSelector?: string;
    fallbacksAttempted?: string[];
    llmResponse?: string;
    confidenceScore?: number;
    success: boolean;
    retryCount: number;
    duration: number;
    error?: string;
}

export function logHealingEvent(data: HealingLogData): void {
    const logLevel = data.success ? 'info' : 'error';
    healingLogger.log(logLevel, `Healing ${data.success ? 'succeeded' : 'failed'} for ${data.elementKey}`, {
        ...data,
        timestamp: new Date().toISOString(),
    });
}

/**
 * Log test step
 */
export function logStep(stepName: string, details?: Record<string, unknown>): void {
    logger.info(`Step: ${stepName}`, details);
}

/**
 * Log error with context
 */
export function logError(message: string, error: Error, context?: Record<string, unknown>): void {
    logger.error(message, {
        error: error.message,
        stack: error.stack,
        ...context,
    });
}

export default logger;
