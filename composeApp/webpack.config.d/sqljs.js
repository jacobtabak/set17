const CopyPlugin = require('copy-webpack-plugin');
const path = require('path');

// sql.js tries to require 'fs' and 'path' which don't exist in the browser
config.resolve.fallback = {
    ...config.resolve.fallback,
    fs: false,
    path: false,
};

// sql.js worker expects sql-wasm.wasm alongside the worker chunk
config.plugins = config.plugins || [];
config.plugins.push(
    new CopyPlugin({
        patterns: [{
            from: path.resolve(__dirname, '../../../../build/wasm/node_modules/sql.js/dist/sql-wasm.wasm'),
            to: config.output.path,
        }],
    })
);

// The @cashapp/sqldelight-sqljs-worker package hardcodes locateFile: file => '/sql-wasm.wasm'
// with an absolute path. This breaks on subpath deployments (e.g. GitHub Pages at /repo-name/).
// Replace with a relative path so it resolves relative to the worker script URL.
config.plugins.push({
    apply(compiler) {
        compiler.hooks.thisCompilation.tap('FixSqlWasmPath', (compilation) => {
            compilation.hooks.processAssets.tap(
                { name: 'FixSqlWasmPath', stage: compiler.webpack.Compilation.PROCESS_ASSETS_STAGE_OPTIMIZE_SIZE + 1 },
                (assets) => {
                    for (const [name, source] of Object.entries(assets)) {
                        if (!name.endsWith('.js')) continue;
                        const code = source.source().toString();
                        if (!code.includes('/sql-wasm.wasm')) continue;
                        const { sources: { RawSource } } = compiler.webpack;
                        compilation.updateAsset(name, new RawSource(
                            code.replace(/(['"])\/(sql-wasm\.wasm)\1/g, '$1$2$1')
                        ));
                    }
                }
            );
        });
    }
});
