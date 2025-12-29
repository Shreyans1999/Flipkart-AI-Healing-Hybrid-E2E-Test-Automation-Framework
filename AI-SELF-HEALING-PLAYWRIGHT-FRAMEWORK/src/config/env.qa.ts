/**
 * QA Environment Configuration
 */
export const qaConfig = {
    baseUrl: 'https://qa.example.com',
    timeout: 30000,
    retries: 2,
    headless: true,
    slowMo: 0,

    // API endpoints
    apiBaseUrl: 'https://qa-api.example.com',

    // Test data
    testUser: {
        username: 'qa-user',
        password: 'QaPassword123!',
    },

    // Logging
    logLevel: 'info',

    // Self-healing settings
    healing: {
        enabled: true,
        maxRetries: 3,
        confidenceThreshold: 0.7,
        autoUpdateLocators: true,
    },
};
