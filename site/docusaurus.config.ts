import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';
import {codeImport} from 'remark-code-import'

const config: Config = {
    title: 'Collektive',
    tagline: 'Aggregate Computing in Kotlin Multiplatform',
    favicon: 'img/collektive-logo.svg',

    // Set the production url of your site here
    url: 'https://collektive.github.io',
    // Set the /<baseUrl>/ pathname under which your site is served
    // For GitHub pages deployment, it is often '/<projectName>/'
    baseUrl: '/',
    trailingSlash: true,

    // GitHub pages deployment config.
    // If you aren't using GitHub pages, you don't need these.
    organizationName: 'Collektive', // Usually your GitHub org/user name.
    projectName: 'collektive', // Usually your repo name.

    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',

    // Even if you don't use internationalization, you can use this field to set
    // useful metadata like html lang. For example, if your site is Chinese, you
    // may want to replace "en" with "zh-Hans".
    i18n: {
        defaultLocale: 'en',
        locales: ['en'],
    },

    presets: [
        [
            'classic',
            {
                docs: {
                    sidebarPath: './sidebars.ts',
                    // Please change this to your repo.
                    // Remove this to remove the "edit this page" links.
                    editUrl:
                        'https://github.com/Collektive/collektive/tree/master/site/',
                    remarkPlugins: [codeImport],
                },
                theme: {
                    customCss: './src/css/custom.css',
                },
            } satisfies Preset.Options,
        ],
    ],

    themeConfig: {
        algolia: {
            // The application ID provided by Algolia
            appId: 'ZAL63W2HTE',

            // Public API key: it is safe to commit it
            apiKey: '4e4fa4774ad84009710a988eaca80e45',

            indexName: 'collektiveio',

            // Optional: see doc section below
            contextualSearch: true,
        },
        // Replace with your project's social card
        image: 'img/collektive-logo.svg',
        navbar: {
            title: 'Collektive',
            logo: {
                alt: 'Collektive Logo',
                src: 'img/collektive-logo.svg',
            },
            items: [
                {
                    type: 'doc',
                    position: 'left',
                    docId: 'intro',
                    label: 'Docs',
                },
                {
                    type: 'dropdown',
                    label: 'APIs',
                    position: 'left',
                    items: [
                        {
                            label: 'dsl',
                            href: 'https://javadoc.io/doc/it.unibo.collektive/dsl/latest/index.html',
                        },
                        {
                            label: 'stdlib',
                            href: 'https://javadoc.io/doc/it.unibo.collektive/stdlib/latest/index.html',
                        },
                    ],
                },
                {
                    type: 'search',
                    position: 'right',
                },
                {
                    href: 'https://github.com/Collektive/collektive',
                    className: 'header-github-link',
                    'aria-label': 'GitHub repository',
                    position: 'right',
                },
            ],
        },
        footer: {
            style: 'dark',
            links: [
                {
                    title: 'Docs',
                    items: [
                        {
                            label: 'Getting Started',
                            to: '/docs/intro',
                        },
                    ],
                },
                {
                    title: 'More',
                    items: [
                        {
                            label: 'GitHub',
                            href: 'https://github.com/Collektive/collektive',
                        },
                    ],
                },
            ],
            copyright: `Copyright © ${new Date().getFullYear()} Collektive, Org. Built with Docusaurus.`,
        },
        prism: {
            theme: prismThemes.github,
            darkTheme: prismThemes.dracula,
        },
        metadata: [
            {name: 'keywords', content: 'cooking, blog'},
            {name: 'twitter:card', content: 'summary_large_image'},
        ],
        headTags: [
            // Declare a <link> preconnect tag
            {
                tagName: 'link',
                attributes: {
                    rel: 'preconnect',
                    href: 'https://collektive.github.io',
                },
            },
            // Declare some json-ld structured data
            {
                tagName: 'script',
                attributes: {
                    type: 'application/ld+json',
                },
                innerHTML: JSON.stringify({
                    '@context': 'https://schema.org/',
                    '@type': 'Organization',
                    name: 'Collektive Org.',
                    url: 'https://collektive.github.io/',
                    logo: 'https://collective.github.io/img/collektive-logo.svg',
                }),
            },
        ],
    } satisfies Preset.ThemeConfig,
};

export default config;
