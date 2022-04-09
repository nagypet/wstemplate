const PROXY_CONFIG = [
    {
        context: [
            "/authenticate",
            "/admin",
            "/actuator",
            "/keystore",
            "/truststore",
            "/logout"
        ],
        target: "http://localhost:8410",
        secure: false
    }
]

module.exports = PROXY_CONFIG;
