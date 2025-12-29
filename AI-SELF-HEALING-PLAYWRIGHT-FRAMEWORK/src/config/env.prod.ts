/**
 * Production Environment Configuration
 */
export const prodConfig = {
    baseUrl: 'https://www.example.com',
    timeout: 30000,
    retries: 2,
    headless: true,
    slowMo: 0,

    // API endpoints
    apiBaseUrl: 'https://api.example.com',

    // Test data - Use environment variables for sensitive data
    testUser: {
        username: process.env.PROD_TEST_USER || '',
        password: process.env.PROD_TEST_PASSWORD || '',
    },

    // Logging
    logLevel: 'warn',

    // Self-healing settings - More conservative in production
    healing: {
        enabled: true,
        maxRetries: 2,
        confidenceThreshold: 0.8,
        autoUpdateLocators: false, // Don't auto-update in prod
    },
};
