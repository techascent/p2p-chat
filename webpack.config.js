const resolve = require('path').resolve;
const webpack = require('webpack');

const BUILD_DIR = resolve(__dirname, 'resources', 'public', 'js');
const APP_DIR =   resolve(__dirname, 'build', 'js');


// Otherwise modules imported from outside this directory does not compile.
// Also needed if modules from this directory were imported elsewhere
// Seems to be a Babel bug
// https://github.com/babel/babel-loader/issues/149#issuecomment-191991686
const BABEL_CONFIG = {
    presets: [
//        'es2015',
//        'react',
//        'stage-2'
    ].map(function configMap(name) {
        return require.resolve(`babel-preset-${name}`);
    })
};

const config = {
    entry: {
        app: resolve('./src/root.js')
    },

    devtool: 'source-map',

    module: {
        rules: [{
            // Compile ES2015 using bable
            test: /\.js$/,
            include: [resolve('.')],
            exclude: [/node_modules/],
            use: [{
                loader: 'babel-loader',
                options: BABEL_CONFIG
            }]
        }]
    },

    devtool: 'eval',
    entry: `${APP_DIR}/main.js`,
    output: {
        path: BUILD_DIR,
        filename: 'bundle.js'
    }
};

module.exports = config;
