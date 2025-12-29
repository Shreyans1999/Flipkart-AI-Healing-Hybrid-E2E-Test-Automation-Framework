/**
 * Development Environment Configuration
 */
export const devConfig = {
    baseUrl: 'https://the-internet.herokuapp.com',
    timeout: 30000,
    retries: 1,
    headless: false,
    slowMo: 0,

    // API endpoints (if applicable)
    apiBaseUrl: '',

    // Test data
    testUser: {
        username: 'tomsmith',
        password: 'SuperSecretPassword!',
    },

    // Logging
    logLevel: 'debug',

    // Self-healing settings
    healing: {
        enabled: true,
        maxRetries: 3,
        confidenceThreshold: 0.6,
        autoUpdateLocators: true,
    },
};
