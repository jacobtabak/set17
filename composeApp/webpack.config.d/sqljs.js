const CopyPlugin = require('copy-webpack-plugin');
const path = require('path');

// sql.js tries to require 'fs' and 'path' which don't exist in the browser
config.resolve.fallback = {
    ...config.resolve.fallback,
    fs: false,
    path: false,
};

// sql.js worker expects sql-wasm.wasm at the web root
config.plugins = config.plugins || [];
config.plugins.push(
    new CopyPlugin({
        patterns: [{
            from: path.resolve(__dirname, '../../../../build/wasm/node_modules/sql.js/dist/sql-wasm.wasm'),
            to: config.output.path,
        }],
    })
);
