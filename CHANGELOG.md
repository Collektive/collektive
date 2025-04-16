## [22.3.0](https://github.com/Collektive/collektive/compare/22.2.0...22.3.0) (2025-04-16)

### Features

* **dsl:** when possible, present the neighbors sorted when stringifying a `Field` ([#901](https://github.com/Collektive/collektive/issues/901)) ([f5568d0](https://github.com/Collektive/collektive/commit/f5568d041115f3d6f667a4f3cd6132d4af2ed8b8))

## [22.2.0](https://github.com/Collektive/collektive/compare/22.1.0...22.2.0) (2025-04-16)

### Features

* **stdlib:** add support for non-Riemann manifolds in `gradientCast` and `distanceTo` ([#900](https://github.com/Collektive/collektive/issues/900)) ([2915f6f](https://github.com/Collektive/collektive/commit/2915f6fdcb7fcc72182501021f8d44bd532306c8))

## [22.1.0](https://github.com/Collektive/collektive/compare/22.0.1...22.1.0) (2025-04-15)

### Features

* provide better alignment clash errors ([#897](https://github.com/Collektive/collektive/issues/897)) ([7b42339](https://github.com/Collektive/collektive/commit/7b42339e8877a494481b6df7aefed3c0a4256c09))

### Dependency updates

* **deps:** update plugin gitsemver to v5 ([e987aea](https://github.com/Collektive/collektive/commit/e987aea32350542bb2ebfe3c22cec80a298b5b21))

### Bug Fixes

* **alchemist-incarnation-collektive:** support Boolean properties ([#898](https://github.com/Collektive/collektive/issues/898)) ([6a4e23e](https://github.com/Collektive/collektive/commit/6a4e23eeebab5a058890359b7e5126b452a6820c))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.17 ([c9c4d49](https://github.com/Collektive/collektive/commit/c9c4d49fb03097620e8b57032be08149a5a1d1b8))

### Style improvements

* **dsl:** rename `evolv`/`e`/`ing` type arguments ([#899](https://github.com/Collektive/collektive/issues/899)) ([de2bc52](https://github.com/Collektive/collektive/commit/de2bc525719f8d9a9e51bed1096f26f2bdd89357))

## [22.0.1](https://github.com/Collektive/collektive/compare/22.0.0...22.0.1) (2025-04-14)

### Dependency updates

* **deps:** update alchemist to v42.0.8 ([287c5ef](https://github.com/Collektive/collektive/commit/287c5ef954581f8e66848151fad61ecec8b247d6))

### Bug Fixes

* **alchemist-incarnation-collektive:** adjust the retention messages and fix internal time ([#891](https://github.com/Collektive/collektive/issues/891)) ([2bee997](https://github.com/Collektive/collektive/commit/2bee997c6f1093bd78c218d1ca959dbea5d8a0b5))

### Build and continuous integration

* **deps:** update actions/setup-node action to v4.4.0 ([7e55f32](https://github.com/Collektive/collektive/commit/7e55f32dd15cdbb97fa79c06e388dea9528be345))

## [22.0.0](https://github.com/Collektive/collektive/compare/21.0.0...22.0.0) (2025-04-12)

### ⚠ BREAKING CHANGES

* add collektive- prefix to all the modules in the project (#892)

### Build and continuous integration

* add collektive- prefix to all the modules in the project ([#892](https://github.com/Collektive/collektive/issues/892)) ([682584a](https://github.com/Collektive/collektive/commit/682584a12e7ab83e494baf945d85ffbba0530e97))

## [21.0.0](https://github.com/Collektive/collektive/compare/20.2.3...21.0.0) (2025-04-11)

### ⚠ BREAKING CHANGES

* **dsl:** make `Field.neighbors` a `Set` (#888)

### Dependency updates

* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.10.0 ([5a4fe34](https://github.com/Collektive/collektive/commit/5a4fe34fa3e1f00cd5dc2b55ae214a25b10ed685))

### Bug Fixes

* **alchemist-incarnation-collektive:** fix a bug preventing `exchanging` to work as expected ([#887](https://github.com/Collektive/collektive/issues/887)) ([da40234](https://github.com/Collektive/collektive/commit/da40234fd12093a9d6b88f6d8ce13ebbddde5552))

### Performance improvements

* **dsl:** make `Field.neighbors` a `Set` ([#888](https://github.com/Collektive/collektive/issues/888)) ([e63552b](https://github.com/Collektive/collektive/commit/e63552b7b44f11bdeb602f0d074420d579e58535))
* **dsl:** minimize the data customization when `exchanging` ([#885](https://github.com/Collektive/collektive/issues/885)) ([01c5312](https://github.com/Collektive/collektive/commit/01c531236aeb1eb3c0a4015839f903d127d8d46e))
* **dsl:** remove useless `toList()` transformation of a `List` ([#890](https://github.com/Collektive/collektive/issues/890)) ([ebb99bb](https://github.com/Collektive/collektive/commit/ebb99bb3c33bbf8f65e51474e6bc470bd00da0f2))
* **dsl:** transform array-backed fields to sequences before mapping ([#889](https://github.com/Collektive/collektive/issues/889)) ([f69df8c](https://github.com/Collektive/collektive/commit/f69df8c04e23118457aa667a1833848adc6aa69b))

### Tests

* **stdlib:** add a convergcast test on collections with custom distance metric ([#886](https://github.com/Collektive/collektive/issues/886)) ([3329f7e](https://github.com/Collektive/collektive/commit/3329f7efb0af1089239bd5dbdb9c089a256a3ea7))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.16 ([d486e63](https://github.com/Collektive/collektive/commit/d486e63c9e4ed0bfda8da789896fedf30c20cdae))

## [20.2.3](https://github.com/Collektive/collektive/compare/20.2.2...20.2.3) (2025-04-10)

### Dependency updates

* **deps:** update alchemist to v42.0.6 ([8c4fe20](https://github.com/Collektive/collektive/commit/8c4fe206ee237532087957e71442c1628fa9efcc))
* **deps:** update alchemist to v42.0.7 ([c19f8f6](https://github.com/Collektive/collektive/commit/c19f8f638b98ca3b6e7b8f6a3958eeaf905cf57b))
* **deps:** update plugin com.gradle.develocity to v4 ([cacd8f0](https://github.com/Collektive/collektive/commit/cacd8f0fc25246e64c55c4d7eebfe81d97ed53f6))
* **deps:** update plugin gitsemver to v4.0.6-dev09-12b3ab5 ([5c3e720](https://github.com/Collektive/collektive/commit/5c3e72058b07746a771b8c45ead5dc9942932078))

### Bug Fixes

* **alchemist-incarnation-collektive:** provide access to the random generator and simplify the API ([#869](https://github.com/Collektive/collektive/issues/869)) ([7eb6f1b](https://github.com/Collektive/collektive/commit/7eb6f1be9ccb3d0bb9d00da5dce1404d69465f9c))
* **deps:** early-force the project version ([c701be8](https://github.com/Collektive/collektive/commit/c701be861b88bfcb0344646f778e1e0ef0e39591))
* **stdlib:** improve `gradientCast` multi-path multi-source management when the maximum path count is constrained ([#873](https://github.com/Collektive/collektive/issues/873)) ([988866d](https://github.com/Collektive/collektive/commit/988866d1205c898da32e8187bbe1667f40b213c5))
* **stdlib:** improve and greatly simplify `gradientCast` ([#879](https://github.com/Collektive/collektive/issues/879)) ([e17def8](https://github.com/Collektive/collektive/commit/e17def8b4bbbc32d689bd3c575bdcfa866b73064))
* **stdlib:** pass a metric `Field` to `bellmanFordGradientCast` instead of a function ([#876](https://github.com/Collektive/collektive/issues/876)) ([7f887a3](https://github.com/Collektive/collektive/commit/7f887a3b7da33d254ccf80bbaed54458b53c6d8b))

### Build and continuous integration

* raise significantly the Mocha timeouts, as stdlib tests require time on js ([04ec446](https://github.com/Collektive/collektive/commit/04ec4469ef16ea0ef68dfc9716057ca42bb622e9))

### Style improvements

* **stdlib:** prefer the `Accumulator` typealias in `distanceTo` ([#875](https://github.com/Collektive/collektive/issues/875)) ([68d7b6b](https://github.com/Collektive/collektive/commit/68d7b6b2ae8fffb97bdff7398e8fd3a897387c9f))

## [20.2.2](https://github.com/Collektive/collektive/compare/20.2.1...20.2.2) (2025-04-09)

### Bug Fixes

* **release:** force dependencies on the compiler-plugin to use the same version of the project ([#868](https://github.com/Collektive/collektive/issues/868)) ([bc01603](https://github.com/Collektive/collektive/commit/bc016031d9e31f2c9efb8c85c6a50cf2fa439c91))

### Build and continuous integration

* **deps:** bump estree-util-value-to-estree in /site ([c1afb97](https://github.com/Collektive/collektive/commit/c1afb979d7ff18847bd90448e528a8b7fdeba4b1))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.15 ([4beb361](https://github.com/Collektive/collektive/commit/4beb361aff51796eccd22b8b82a99dfcf5a5e619))

## [20.2.1](https://github.com/Collektive/collektive/compare/20.2.0...20.2.1) (2025-04-08)

### Bug Fixes

* **alchemist-incarnation-collektive:** make the DSL and the stdlib API-level dependencies to simplify the import ([#866](https://github.com/Collektive/collektive/issues/866)) ([893e988](https://github.com/Collektive/collektive/commit/893e9888fd91c21a56634cdaa148d78602bcae43))

## [20.2.0](https://github.com/Collektive/collektive/compare/20.1.0...20.2.0) (2025-04-08)

### Features

* **stdlib:** implement `boundedElection` ([9c4e693](https://github.com/Collektive/collektive/commit/9c4e693fe794d204c74ce3fc80e0c869414ddd7f))

### Dependency updates

* **deps:** update plugin publishoncentral to v8.0.6 ([13b2e17](https://github.com/Collektive/collektive/commit/13b2e1788cebd67f463e3d1a20f799c3e3cba2fb))

## [20.1.0](https://github.com/Collektive/collektive/compare/20.0.0...20.1.0) (2025-04-08)

### Features

* **stdlib:** implement convergeCast ([#848](https://github.com/Collektive/collektive/issues/848)) ([ac15a7c](https://github.com/Collektive/collektive/commit/ac15a7ce0b61e9e648180ea90756a7a70e3fb0cb))

## [20.0.0](https://github.com/Collektive/collektive/compare/19.1.0...20.0.0) (2025-04-08)

### ⚠ BREAKING CHANGES

* **stdlib:** fix hop-distance metric and general metric usage in gradient casts (#857)

### Features

* **dsl:** add `collect` and `collectDistinct` to the field reduction operations ([#852](https://github.com/Collektive/collektive/issues/852)) ([f00f386](https://github.com/Collektive/collektive/commit/f00f386ce8104b3d448c06e4af41579b6913a5f3))
* **test-tooling:** add quick node-access method to `Environment` ([#855](https://github.com/Collektive/collektive/issues/855)) ([08c1610](https://github.com/Collektive/collektive/commit/08c1610ab89a7aab7393920a6bd2653b667c7d39))

### Dependency updates

* **deps:** update dependency react-router-dom to v7.5.0 ([04f9832](https://github.com/Collektive/collektive/commit/04f98325cdbabebccfd02f7d9356409cd5b6c531))
* **deps:** update dependency typescript to v5.8.3 ([7a87937](https://github.com/Collektive/collektive/commit/7a8793743595c7644b5d941d6f024c8e8433d1f8))

### Bug Fixes

* **dsl:** fix bugged management of overrides, with undesired misalignment in case of `exchange` ([#853](https://github.com/Collektive/collektive/issues/853)) ([f4f87f3](https://github.com/Collektive/collektive/commit/f4f87f35c1c364cd72db1127dbb0b31ed0289711))
* **stdlib:** fix hop-distance metric and general metric usage in gradient casts ([#857](https://github.com/Collektive/collektive/issues/857)) ([685137c](https://github.com/Collektive/collektive/commit/685137c5b48781b07df54a2f2cc049d96dc89199))
* **test-tooling:** fix bugged message preparation ignoring `exchange`d messages ([#854](https://github.com/Collektive/collektive/issues/854)) ([a18542f](https://github.com/Collektive/collektive/commit/a18542fb67413e1e9cb3f0310fdf59a1b2f95938))

### Build and continuous integration

* **deps:** update github/codeql-action action to v3.28.15 ([292cb9f](https://github.com/Collektive/collektive/commit/292cb9f253bd7c45fc804e73d7e6fa02d989e1f2))

### Style improvements

* **dsl:** sort `Field` members ([#851](https://github.com/Collektive/collektive/issues/851)) ([5169c27](https://github.com/Collektive/collektive/commit/5169c2767ac42f83466c05810d024332a013ce84))
* **stdlib:** fix linguistic antipattern ([#856](https://github.com/Collektive/collektive/issues/856)) ([48c2bc7](https://github.com/Collektive/collektive/commit/48c2bc773c2dc78b17d0b101cdd396ebdccf1b3c))

## [19.1.0](https://github.com/Collektive/collektive/compare/19.0.0...19.1.0) (2025-04-07)

### Features

* **dsl:** generalize `exchanging` ([#850](https://github.com/Collektive/collektive/issues/850)) ([e961a35](https://github.com/Collektive/collektive/commit/e961a35c55c19f3ea9d89bf21f5b9bea4058c2f8))

### Build and continuous integration

* **deps:** update github/codeql-action action to v3.28.14 ([82f3a90](https://github.com/Collektive/collektive/commit/82f3a90b1ca3c3e7ea053b68d5af08cafc7e755f))

## [19.0.0](https://github.com/Collektive/collektive/compare/18.0.0...19.0.0) (2025-04-04)

### ⚠ BREAKING CHANGES

* **stdlib:** refactor all spreading operators (#841)

### Features

* **stdlib:** refactor all spreading operators ([#841](https://github.com/Collektive/collektive/issues/841)) ([be9c094](https://github.com/Collektive/collektive/commit/be9c09491523c2683ec7336c919c3633c40b5f34))

### Build and continuous integration

* allow ten seconds for js tests ([cbb4589](https://github.com/Collektive/collektive/commit/cbb458931e287fe830a9a83d9ab97225123fbe66))
* remove multijvm from buildSrc ([7cefd4a](https://github.com/Collektive/collektive/commit/7cefd4adac98710f42c99494696957613d68d128))

## [18.0.0](https://github.com/Collektive/collektive/compare/17.2.2...18.0.0) (2025-04-03)

### ⚠ BREAKING CHANGES

* **dsl:** revisit the core API of Collektive for clarity and coherence (#847)

### General maintenance

* **dsl:** update copyright header ([377431b](https://github.com/Collektive/collektive/commit/377431ba8e27ce161b00ac55abeab2f79d64a809))

### Refactoring

* **dsl:** revisit the core API of Collektive for clarity and coherence ([#847](https://github.com/Collektive/collektive/issues/847)) ([a77a0af](https://github.com/Collektive/collektive/commit/a77a0af7c465ff8b4dde4931af97ee632598ebc7))

## [17.2.2](https://github.com/Collektive/collektive/compare/17.2.1...17.2.2) (2025-04-02)

### Dependency updates

* **deps:** update dependency com.github.gmazzo.buildconfig to v5.6.2 ([3a1da14](https://github.com/Collektive/collektive/commit/3a1da14d2085d36689f3ac06a27eac3e761d4992))

### Bug Fixes

* **compiler-plugin:** check the presence of an aggregate context before triggering "loop without align" warnings ([#843](https://github.com/Collektive/collektive/issues/843)) ([9253e35](https://github.com/Collektive/collektive/commit/9253e3523a1563e6e81834ddbd9cc2daf0cc76d0))

### Documentation

* **dsl:** write a minimal documentation for `NeighborsData` ([#846](https://github.com/Collektive/collektive/issues/846)) ([1ab4a83](https://github.com/Collektive/collektive/commit/1ab4a83446288cc618c5d4519b3985c5e4738775))

### Build and continuous integration

* **deps:** bump image-size from 1.1.1 to 1.2.1 in /site ([#844](https://github.com/Collektive/collektive/issues/844)) ([b149889](https://github.com/Collektive/collektive/commit/b149889c94a0c2429a4165bc4485c522288b1531))

### General maintenance

* update some copyright headers ([4730605](https://github.com/Collektive/collektive/commit/473060509f3b28855492065fd09058a45af668fd))

## [17.2.1](https://github.com/Collektive/collektive/compare/17.2.0...17.2.1) (2025-04-01)

### Dependency updates

* **core-deps:** update serialization to v1.8.1 ([e205d1b](https://github.com/Collektive/collektive/commit/e205d1b9d68a0d7ca12ec7e885d1ead3731b6029))
* **deps:** update alchemist to v42.0.2 ([7605f81](https://github.com/Collektive/collektive/commit/7605f812eb743967e1fc9a5cb2bce0fa54f354b9))
* **deps:** update alchemist to v42.0.3 ([b4b0e82](https://github.com/Collektive/collektive/commit/b4b0e825534bf34e9547c9975da38aa176ffbfd4))
* **deps:** update alchemist to v42.0.4 ([df14cc2](https://github.com/Collektive/collektive/commit/df14cc278e983aa9fe2337c83ef403f4486cce27))
* **deps:** update alchemist to v42.0.5 ([afd6909](https://github.com/Collektive/collektive/commit/afd690978dd936474cdd3ebb6443530eb4ecef95))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.6.0 ([17f4861](https://github.com/Collektive/collektive/commit/17f4861dca87590b3e9a3d989f09b45beb3f98dc))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.6.1 ([2aa0d76](https://github.com/Collektive/collektive/commit/2aa0d7648ec6869182cd8dfd456e83a67aa5dfbf))
* **deps:** update dependency react-router-dom to v7.4.1 ([eeaef95](https://github.com/Collektive/collektive/commit/eeaef9526e3539b31aefac257070025f61a13727))
* **deps:** update plugin kotlin-qa to v0.78.2 ([4845a5c](https://github.com/Collektive/collektive/commit/4845a5c0f285755b0d27bcfc5cae77b0fcfb093e))
* **deps:** update plugin kotlin-qa to v0.78.3 ([e09f009](https://github.com/Collektive/collektive/commit/e09f009ffb3a3a6799cf8acb432a67852454bdb2))
* **deps:** update plugin kotlin-qa to v0.84.1 ([c83526a](https://github.com/Collektive/collektive/commit/c83526ada4fed6c1afae074dcee3478390f8f21f))
* **deps:** update plugin kotlin-qa to v0.85.0 ([0091c34](https://github.com/Collektive/collektive/commit/0091c340141d8d4dd96015d957f1c575b39383d4))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.14 ([0d8747c](https://github.com/Collektive/collektive/commit/0d8747c3cb280b921a0c2c86b66869cd89260203))
* **deps:** update github/codeql-action action to v3.28.13 ([446e97c](https://github.com/Collektive/collektive/commit/446e97c1ba71ae7edf8a999e6d52b41641d4f322))

## [17.2.0](https://github.com/Collektive/collektive/compare/17.1.3...17.2.0) (2025-03-24)

### Features

* **alchemist-incarnation:** allow access to `CollektiveDevice` properties from within Alchemist's molecule "properties" ([#823](https://github.com/Collektive/collektive/issues/823)) ([3f4077f](https://github.com/Collektive/collektive/commit/3f4077f89cf23e9276da55f1745afeebb4b9d2d0))

### Dependency updates

* **deps:** update plugin publishoncentral to v8.0.5 ([d009dbc](https://github.com/Collektive/collektive/commit/d009dbca01ec74eb294142e1e16e43d16ecc6132))

### Tests

* testing incarnation implementation via gradient ([#821](https://github.com/Collektive/collektive/issues/821)) ([8c24a3a](https://github.com/Collektive/collektive/commit/8c24a3aaca39b3fa7cb3b8ec5ce7fa78c4488745))

## [17.1.3](https://github.com/Collektive/collektive/compare/17.1.2...17.1.3) (2025-03-23)

### Dependency updates

* **core-deps:** update kotlin monorepo to v2.1.20 ([258c55e](https://github.com/Collektive/collektive/commit/258c55e186e10cc7f85d6f6fb23459c167f1b57b))
* **deps:** update dependency remark-code-import to v1.1.1 ([e34bdf9](https://github.com/Collektive/collektive/commit/e34bdf9fdb7dfc2b2ec613d3612a2a3697608373))
* **deps:** update dependency remark-code-import to v1.2.0 ([f3a3a85](https://github.com/Collektive/collektive/commit/f3a3a852c84c1c22ec90ed015ebce39dc88f0acb))
* **deps:** update plugin multijvmtesting to v3.2.4 ([70d1cdb](https://github.com/Collektive/collektive/commit/70d1cdb9c3310b5d5963a36f492b6544c1945a2e))
* **deps:** update plugin multijvmtesting to v3.3.0 ([aac80b8](https://github.com/Collektive/collektive/commit/aac80b8451d1f0d2af26572cd1dcac4d75826911))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.22 ([1f84f9e](https://github.com/Collektive/collektive/commit/1f84f9e47aecad00717f90194fe7dd027ed6d469))

### Build and continuous integration

* **deps:** autid fix for npm dependencies ([0d9d696](https://github.com/Collektive/collektive/commit/0d9d69621800784c9d08f4101a4d34d87e3e903f))
* **site:** add code-import remark plugin for embedding code snippets from file ([741cf43](https://github.com/Collektive/collektive/commit/741cf4324782c65b88750d8cad6cdeea388c478d))

### Style improvements

* reformat code ([d2eded6](https://github.com/Collektive/collektive/commit/d2eded6719421153ef4978fe0bf7c3e19f55a5ee))
* reformat code ([a33146b](https://github.com/Collektive/collektive/commit/a33146b8fa974f2a1955c337ed5150bed64db7aa))

### Refactoring

* extract required configuration keys into a method for check and set ([ccce759](https://github.com/Collektive/collektive/commit/ccce759173c9e0908387126a787b694e92bbd9f8))
* improve code quality ([2d0c06a](https://github.com/Collektive/collektive/commit/2d0c06ae986938dd8878fec13242beee0e58108b))
* use api replacement for removed keys ([6410bac](https://github.com/Collektive/collektive/commit/6410bacce52eec1acd8aa248d396c906126aa340))

## [17.1.2](https://github.com/Collektive/collektive/compare/17.1.1...17.1.2) (2025-03-22)

### Dependency updates

* **deps:** update dependency react-markdown to v10 ([f9369ef](https://github.com/Collektive/collektive/commit/f9369ef6307fe3d629a2d55a41d0a7374102571e))
* **deps:** update dependency react-markdown to v9.1.0 ([d0b8436](https://github.com/Collektive/collektive/commit/d0b8436a604c89ad566a45c59e63449d60834866))
* **deps:** update dependency react-router-dom to v7 ([f574d26](https://github.com/Collektive/collektive/commit/f574d26b8b2a1770b1ea4020bde6486e1b137d8f))

### Documentation

* add preliminary knowledge about collektive and the paradigm ([#548](https://github.com/Collektive/collektive/issues/548)) ([d9fdd88](https://github.com/Collektive/collektive/commit/d9fdd88836e71cd40c7596904da21a1c29b1b38f))

## [17.1.1](https://github.com/Collektive/collektive/compare/17.1.0...17.1.1) (2025-03-22)

### Bug Fixes

* force version override if passed as property ([c29cd33](https://github.com/Collektive/collektive/commit/c29cd33cd1bba41ef468cfe2ea4eba7b0b15e44c))

## [17.1.0](https://github.com/Collektive/collektive/compare/17.0.2...17.1.0) (2025-03-21)

### Features

* **dsl:** add `Field.contains` to enable the `in` operator ([#746](https://github.com/Collektive/collektive/issues/746)) ([927bd65](https://github.com/Collektive/collektive/commit/927bd65f676537a03990001d39bd3c20b3ca6bdb))

### Bug Fixes

* **build:** set version number when publishing gradle plugin ([052f362](https://github.com/Collektive/collektive/commit/052f3629ddd137d904d0b501b84e9de856351745))

### Build and continuous integration

* enable power-assert ([#778](https://github.com/Collektive/collektive/issues/778)) ([ef1810c](https://github.com/Collektive/collektive/commit/ef1810ca2b3a3f3afdad6df0cf6c0d3ee07a9880))

## [17.0.2](https://github.com/Collektive/collektive/compare/17.0.1...17.0.2) (2025-03-21)

### Performance improvements

* use full path factory in simulation instead crypto paths ([2505a5c](https://github.com/Collektive/collektive/commit/2505a5c85f6e56a0a440f01e6a429aca58adb344))

### Build and continuous integration

* **deps:** update actions/download-artifact action to v4.2.1 ([c5844df](https://github.com/Collektive/collektive/commit/c5844df23eb52cb0ca0ff9bb2fc1a048874e6fb0))
* **deps:** update actions/setup-node action to v4.3.0 ([56f35f5](https://github.com/Collektive/collektive/commit/56f35f58a6d3b878069644646bc87572536dcf4b))
* **deps:** update actions/upload-artifact action to v4.6.2 ([101bc59](https://github.com/Collektive/collektive/commit/101bc59b6267ad30126c5de1798efb0b36d742d1))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.13 ([eb8af7a](https://github.com/Collektive/collektive/commit/eb8af7adb64cd02125d23240e8a4e3aff9e33322))
* **deps:** update github/codeql-action action to v3.28.12 ([cc81ae1](https://github.com/Collektive/collektive/commit/cc81ae1026cb03fc5f7a28fc0dcbd0d80c8bbf6f))

### Style improvements

* format files according to intellij_idea style ([#805](https://github.com/Collektive/collektive/issues/805)) ([69a9941](https://github.com/Collektive/collektive/commit/69a9941f39d255f0e03f89dd098cd84cde93438d))

## [17.0.1](https://github.com/Collektive/collektive/compare/17.0.0...17.0.1) (2025-03-17)

### Bug Fixes

* properly set the sender ID to OutboundEnvelope when extracting the message for the neighbor ([75c38c3](https://github.com/Collektive/collektive/commit/75c38c3c78bccaa8ff061f406fe321c802f7e74d))

## [17.0.0](https://github.com/Collektive/collektive/compare/16.1.1...17.0.0) (2025-03-16)

### ⚠ BREAKING CHANGES

* remove uneeded device id from mailbox and implements networks supporting exchange communication pattern

### Dependency updates

* **deps:** update alchemist to v39.0.1 ([fefbe9a](https://github.com/Collektive/collektive/commit/fefbe9a1162a9f5088bf44adce449d1fe365f309))
* **deps:** update alchemist to v40 ([6768920](https://github.com/Collektive/collektive/commit/6768920f9623610266c3764e51eb3f1d33a60725))
* **deps:** update alchemist to v40.1.2 ([9c8873e](https://github.com/Collektive/collektive/commit/9c8873ec74ed779a0f0fecaa81f1bc43a3a4ec7a))
* **deps:** update alchemist to v41 ([4b2ea56](https://github.com/Collektive/collektive/commit/4b2ea56e6cf00bd4502bfe6afc4f2cd592ff03f2))
* **deps:** update alchemist to v42 ([2585114](https://github.com/Collektive/collektive/commit/2585114b4daa1a7af2f5218f67c42fe90e42e3e0))
* **deps:** update alchemist to v42.0.1 ([96817a8](https://github.com/Collektive/collektive/commit/96817a85dda843e48c6874fe55b8106a1235480a))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.5.2 ([57e9b0e](https://github.com/Collektive/collektive/commit/57e9b0e33857af5579265b0fc535d8425e0907b6))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.5.4 ([5bca26b](https://github.com/Collektive/collektive/commit/5bca26b962c249399013cf801857004bb13153a9))
* **deps:** update dependency com.squareup:kotlinpoet to v2.1.0 ([c22a719](https://github.com/Collektive/collektive/commit/c22a71915552fec11a6b55b4c66f207ff5ccde62))
* **deps:** update dependency gradle to v8.13 ([c4b0382](https://github.com/Collektive/collektive/commit/c4b03820273f5b5f0c55db78358f8782f74582f6))
* **deps:** update dependency it.unibo.alchemist:alchemist-api to v40.1.3 ([21ab352](https://github.com/Collektive/collektive/commit/21ab352e666385350d0c7174430fdd82a3290114))
* **deps:** update dependency org.kotlincrypto.hash:sha3 to v0.7.0 ([c3dbe79](https://github.com/Collektive/collektive/commit/c3dbe790db85642a1fe1af230593d520a5694dcb))
* **deps:** update dependency org.slf4j:slf4j-api to v2.0.17 ([58a6200](https://github.com/Collektive/collektive/commit/58a6200205dd6e5591136c21be6906ccc142e14e))
* **deps:** update dependency typescript to ~5.8.0 ([7618cc0](https://github.com/Collektive/collektive/commit/7618cc029e9d8805dea8237c2ff4ba799e660883))
* **deps:** update plugin multijvmtesting to v3.2.3 ([1b53280](https://github.com/Collektive/collektive/commit/1b532807775466828b1810402169bbe75e120eaa))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.21 ([e6d66fc](https://github.com/Collektive/collektive/commit/e6d66fc93c3404e7de0bef2d0138da92fd6715dd))
* **deps:** update plugin publishoncentral to v8.0.4 ([c455f1e](https://github.com/Collektive/collektive/commit/c455f1e0d7a38b8dee7581435f35617ab479c1b7))
* **deps:** update plugin tasktree to v4.0.1 ([bf91b95](https://github.com/Collektive/collektive/commit/bf91b95d34605d6a965fe4e0a2205100c37cb584))

### Bug Fixes

* remove uneeded device id from mailbox and implements networks supporting exchange communication pattern ([9723a02](https://github.com/Collektive/collektive/commit/9723a02999b3dda83fdf45eca1d8d9c90e8c0cb1))

### Build and continuous integration

* **deps:** update actions/download-artifact action to v4.1.9 ([6f53881](https://github.com/Collektive/collektive/commit/6f538817da8135808c0754b23bda2b142d32b158))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.12 ([3f0367e](https://github.com/Collektive/collektive/commit/3f0367e4f4bc20088179bff94f5f0a06bbe5bee3))
* **deps:** update github/codeql-action action to v3.28.11 ([038b0c8](https://github.com/Collektive/collektive/commit/038b0c8d93629f09da8c5ae5258aa99d1e42a528))

## [16.1.1](https://github.com/Collektive/collektive/compare/16.1.0...16.1.1) (2025-02-22)

### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.125 ([48bbb51](https://github.com/Collektive/collektive/commit/48bbb519a8abe9710a86f946681137a2b52012a9))

### Bug Fixes

* add delicate api annotation to `projection` method ([aabd16f](https://github.com/Collektive/collektive/commit/aabd16f920d89e32ef874fa3feca50b5975934d8))

### Build and continuous integration

* **deps:** update actions/upload-artifact action to v4.6.1 ([4f94f9f](https://github.com/Collektive/collektive/commit/4f94f9f804d90f558388ff016ad983c9dec465e7))
* **deps:** update github/codeql-action action to v3.28.10 ([56eb8d5](https://github.com/Collektive/collektive/commit/56eb8d5d0c1313245e1ad1b64abcf7b33627395d))
* drop unused bundle alias ([#780](https://github.com/Collektive/collektive/issues/780)) ([215f033](https://github.com/Collektive/collektive/commit/215f033589eceac7fd1a57e4a2923096edaffabd))

## [16.1.0](https://github.com/Collektive/collektive/compare/16.0.1...16.1.0) (2025-02-20)

### Features

* introduce opt-in annotation preventing the erroneous usage of API intended to be internal ([#777](https://github.com/Collektive/collektive/issues/777)) ([9aeb063](https://github.com/Collektive/collektive/commit/9aeb06358ef1d51d60bc38496418bdb81732e56c))

### Dependency updates

* **deps:** update alchemist to v38.0.1 ([8629445](https://github.com/Collektive/collektive/commit/8629445df4f43f63f1060b41a6ae6d1f8b914db9))
* **deps:** update alchemist to v38.0.3 ([a3e7490](https://github.com/Collektive/collektive/commit/a3e74904c2cfb1725205afa53aaca619c0e975bd))
* **deps:** update alchemist to v39 ([b683afc](https://github.com/Collektive/collektive/commit/b683afc81146b1712db5819884844b3fd870b572))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.124 ([3875293](https://github.com/Collektive/collektive/commit/38752935b4af7b0c19fdbc3a0911690e4acaeb6d))
* **deps:** update plugin com.gradle.develocity to v3.19.2 ([36104c0](https://github.com/Collektive/collektive/commit/36104c02a2bfe42be49905db83126f217e8104c3))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.10 ([0babc68](https://github.com/Collektive/collektive/commit/0babc68992ea527e6228e50cf61ce0582385e49d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.9 ([6ba1df3](https://github.com/Collektive/collektive/commit/6ba1df30954cce8b72d24b43dd59ac7555e57511))

## [16.0.1](https://github.com/Collektive/collektive/compare/16.0.0...16.0.1) (2025-02-16)

### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.123 ([3732329](https://github.com/Collektive/collektive/commit/3732329cb03dda6dbd6f87aeb74d42e4dd9da7bd))
* **deps:** update node.js to 22.14 ([a4df604](https://github.com/Collektive/collektive/commit/a4df604bbc3ca2de2a8902a7693fb4db4d30070b))

### Bug Fixes

* correct `maxWithSelf` implementation to use max instead of min ([58a449f](https://github.com/Collektive/collektive/commit/58a449f79fb9cb8965d2d562ac95beb2c562e8e4))

### Tests

* regression test for issue [#767](https://github.com/Collektive/collektive/issues/767) ([5ea04bd](https://github.com/Collektive/collektive/commit/5ea04bdd146c348417920f24df9449da1aee127c))
* remove kotest from `dsl` module ([#765](https://github.com/Collektive/collektive/issues/765)) ([6ac2403](https://github.com/Collektive/collektive/commit/6ac2403757dc92ddb2e70c4955d2a6791c8815be))
* testing protobuf serializer ([03bbebc](https://github.com/Collektive/collektive/commit/03bbebc48c3bbc9e55c138a027e158f2eee9009b))

### Build and continuous integration

* use core serialization library in the DSL and specific serialization formats in the tests ([41c3a91](https://github.com/Collektive/collektive/commit/41c3a91d850fe01ec033b6edbf7337774cd9cc6f))

## [16.0.0](https://github.com/Collektive/collektive/compare/15.0.0...16.0.0) (2025-02-11)

### ⚠ BREAKING CHANGES

* capture the concept of data sharing method as part of the API (#764)

### Features

* capture the concept of data sharing method as part of the API ([#764](https://github.com/Collektive/collektive/issues/764)) ([658a0b6](https://github.com/Collektive/collektive/commit/658a0b6de5b3133e2c28d7bbf28c679782a39162))

## [15.0.0](https://github.com/Collektive/collektive/compare/14.0.0...15.0.0) (2025-02-11)

### ⚠ BREAKING CHANGES

* use `KSerializer` instead of `KClass` for serializing also compound types (#763)

### Dependency updates

* **deps:** update alchemist to v37.3.1 ([174408a](https://github.com/Collektive/collektive/commit/174408a8f29c636ca117a8c8d43971f5c14000a5))
* **deps:** update alchemist to v38 ([511520b](https://github.com/Collektive/collektive/commit/511520bf03bfb6e58a4116e9cce28beeeb2760ad))
* **deps:** update dependency org.kotlincrypto.hash:sha3 to v0.6.1 ([9217f20](https://github.com/Collektive/collektive/commit/9217f204cdb80d8f357103da00f62c0211617a0e))
* **deps:** update kotest to v6.0.0.m2 ([ec30cea](https://github.com/Collektive/collektive/commit/ec30cea0e5d1b55da4a0066bb2cabbbed7869d32))

### Tests

* **compiler-plugin-test:** migrate a couple of tests to kotlin.test ([#761](https://github.com/Collektive/collektive/issues/761)) ([0d768b3](https://github.com/Collektive/collektive/commit/0d768b3c433a630ab2dfe58d8d3d265f56516919))

### Refactoring

* use `KSerializer` instead of `KClass` for serializing also compound types ([#763](https://github.com/Collektive/collektive/issues/763)) ([e80cbd6](https://github.com/Collektive/collektive/commit/e80cbd602dc8a0625f84bf275445829c2b3f9170))

## [14.0.0](https://github.com/Collektive/collektive/compare/13.1.1...14.0.0) (2025-02-10)

### ⚠ BREAKING CHANGES

* ready the DSL and boundary for kotlinx.serialization (#753)

### Dependency updates

* **deps:** update alchemist to v37.1.7 ([12cc235](https://github.com/Collektive/collektive/commit/12cc2351dc161da9536d8ac97cfb4d11079022dd))
* **deps:** update alchemist to v37.1.8 ([21080e1](https://github.com/Collektive/collektive/commit/21080e1934ea179902bd1aae3a55d2a3c1fc1068))
* **deps:** update alchemist to v37.2.0 ([ffab524](https://github.com/Collektive/collektive/commit/ffab524923203caa8d49ff738e62ff49dceb46b6))
* **deps:** update alchemist to v37.2.1 ([777b54a](https://github.com/Collektive/collektive/commit/777b54a8d26f16887c3fe1a922c5f74a84a6b8b9))
* **deps:** update alchemist to v37.3.0 ([e1b6746](https://github.com/Collektive/collektive/commit/e1b6746eca227fb86a1ea649e4aa71c24ba76c61))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.120 ([91da80c](https://github.com/Collektive/collektive/commit/91da80c1cb3e2aab68117b6a55dd4aa540ee8d4c))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.121 ([1dd4ee9](https://github.com/Collektive/collektive/commit/1dd4ee9c26fa7d28a7fd98e0d24140d1f4f37547))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.122 ([fbae1ca](https://github.com/Collektive/collektive/commit/fbae1ca38377c356910516097d5b99394b5d6472))
* **deps:** update plugin publishoncentral to v8.0.3 ([b9a0851](https://github.com/Collektive/collektive/commit/b9a085179114b8c3c2ccecd4e3eeb1713bd7c151))

### Build and continuous integration

* **deps:** update github/codeql-action action to v3.28.9 ([78951d0](https://github.com/Collektive/collektive/commit/78951d0f27b6705d1126956c3f02bb4bec4fdd88))

### Refactoring

* ready the DSL and boundary for kotlinx.serialization ([#753](https://github.com/Collektive/collektive/issues/753)) ([512d438](https://github.com/Collektive/collektive/commit/512d4383457a4b96a4e0a0a91ac9eea2430234c5))

## [13.1.1](https://github.com/Collektive/collektive/compare/13.1.0...13.1.1) (2025-01-31)

### Documentation

* **dsl:** 🇬🇧 ➡️ 🇺🇸 ([e37116c](https://github.com/Collektive/collektive/commit/e37116c587e1cb6769299ec80e73003b5bf83d9a))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.8 ([96c74a0](https://github.com/Collektive/collektive/commit/96c74a0d03df5ad05fb170b208590390ecf1ffb0))

## [13.1.0](https://github.com/Collektive/collektive/compare/13.0.0...13.1.0) (2025-01-30)

### Features

* **stdlib:** write a `multiGradientCast` ([cc9f35f](https://github.com/Collektive/collektive/commit/cc9f35fe523a0a7a86920f2a9f7c8c61f5c588aa))

### Dependency updates

* **deps:** update plugin gradlepluginpublish to v1.3.1 ([e57b550](https://github.com/Collektive/collektive/commit/e57b550f8fdbfeb0ec312e913b09c5ccffd92675))

### Documentation

* **stdlib:** document the rising value problem ([caac4b5](https://github.com/Collektive/collektive/commit/caac4b537cbe26dc3dd21b9cbe71e059f72b36ca))

### Build and continuous integration

* **deps:** update github/codeql-action action to v3.28.7 ([fb7d337](https://github.com/Collektive/collektive/commit/fb7d3372b3e5d7e15b5071c33effc0fbfcc2874c))
* **deps:** update github/codeql-action action to v3.28.8 ([5f8fc0c](https://github.com/Collektive/collektive/commit/5f8fc0c2767eb3400be49c7777c50b5d7b0e591d))

### Style improvements

* **stdlib:** name the lambda parameters for clarity (thanks [@cric96](https://github.com/cric96)) ([500364f](https://github.com/Collektive/collektive/commit/500364ff39bbbba3743325f576740978d7fa6914))

## [13.0.0](https://github.com/Collektive/collektive/compare/12.3.3...13.0.0) (2025-01-29)

### ⚠ BREAKING CHANGES

* **stdlib:** revisit the base structure of the standard library

### Dependency updates

* **deps:** update alchemist to v37.1.6 ([4430611](https://github.com/Collektive/collektive/commit/4430611d5818c9b5f8f330c07341d98dfb09fb76))
* **deps:** update plugin gitsemver to v4.0.1 ([12cd867](https://github.com/Collektive/collektive/commit/12cd8675a16ae0a8a3dd98f0831117d676c8322e))
* **deps:** update plugin gitsemver to v4.0.2 ([74626e1](https://github.com/Collektive/collektive/commit/74626e1f3a8c81820cbf7cf44c8cceb748d2afd7))
* **deps:** update plugin multijvmtesting to v3.2.2 ([8adeac7](https://github.com/Collektive/collektive/commit/8adeac7d9b213f10ed4cb969170f859644a473be))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.20 ([53e77d9](https://github.com/Collektive/collektive/commit/53e77d95a95cdff197d0ea08edc6d6286c36e3ea))

### Bug Fixes

* **stdlib:** coerce all spread distances within the provided distance range, and verify the triangle inequality ([c61736f](https://github.com/Collektive/collektive/commit/c61736ff8025c266c8d38eceaefafcfed359575c))

### Tests

* **stdlib:** drop kotest, use kotlin.test instead ([39ab094](https://github.com/Collektive/collektive/commit/39ab09454cb64db0ef0343116dacfdbe1e1672fc))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.7 ([de60db0](https://github.com/Collektive/collektive/commit/de60db0843936bd7e5cbde8ada9a7534b575c809))
* **deps:** update github/codeql-action action to v3.28.6 ([9607dd7](https://github.com/Collektive/collektive/commit/9607dd75370e3a24c69a1fc26f41480959f03842))
* **stdlib:** enable [power-assert](https://kotlinlang.org/docs/power-assert.html) ([50486d0](https://github.com/Collektive/collektive/commit/50486d06863d674ba08af01338cdca8c139a9566))

### Refactoring

* **stdlib:** revisit the base structure of the standard library ([eacdded](https://github.com/Collektive/collektive/commit/eacdded614b76950660dcd4fc4fa48bc2872c84d))

## [12.3.3](https://github.com/Collektive/collektive/compare/12.3.2...12.3.3) (2025-01-27)

### Dependency updates

* **core-deps:** update plugin collektivize to v1.0.8 ([a0ff5ae](https://github.com/Collektive/collektive/commit/a0ff5ae12be5cdbce63daaf7fe4b04af59d79a66))
* **deps:** update plugin gitsemver to v4 ([f050f68](https://github.com/Collektive/collektive/commit/f050f68d548cd0d48d29af2b0b5807bbce14b5fa))

## [12.3.2](https://github.com/Collektive/collektive/compare/12.3.1...12.3.2) (2025-01-27)

### Dependency updates

* **core-deps:** update kotlin monorepo to v2.1.10 ([34b7924](https://github.com/Collektive/collektive/commit/34b792412db5c97e720fdcd01d2555953c7eaaef))
* **deps:** update plugin gitsemver to v3.1.10 ([57ffecd](https://github.com/Collektive/collektive/commit/57ffecd3d3b30a80643e4d9408ca4044c4bde434))
* **deps:** update plugin gitsemver to v3.1.8 ([8938d8b](https://github.com/Collektive/collektive/commit/8938d8bf76ce776c3faf9e65c53623980c1c446f))
* **deps:** update plugin gitsemver to v3.1.9 ([c9fd90a](https://github.com/Collektive/collektive/commit/c9fd90ac211e53731df4da244520b87afd25e654))

### Build and continuous integration

* **deps:** update actions/setup-node action to v4.2.0 ([0af84f3](https://github.com/Collektive/collektive/commit/0af84f363a608a42a6ed0a6acf3d1181bfbf2c1f))

## [12.3.1](https://github.com/Collektive/collektive/compare/12.3.0...12.3.1) (2025-01-26)

### Dependency updates

* **core-deps:** update plugin collektivize to v1.0.7 ([fce6a7a](https://github.com/Collektive/collektive/commit/fce6a7a3fef067de4a875c2fecf3285696fcd5ea))
* **deps:** update alchemist to v37.1.0 ([4d56665](https://github.com/Collektive/collektive/commit/4d566651a1f43edc79c5e9027614f3e39116cfcd))
* **deps:** update alchemist to v37.1.1 ([b8350ac](https://github.com/Collektive/collektive/commit/b8350ac5ea976a5c8e8896a428b3ad6b9d28d658))
* **deps:** update alchemist to v37.1.2 ([9ac6389](https://github.com/Collektive/collektive/commit/9ac63890509c0a57c17b87f0d73f2082ed3d1a25))
* **deps:** update alchemist to v37.1.3 ([37b38ab](https://github.com/Collektive/collektive/commit/37b38abb3c7992ff853f13aa78a448c2e5a3b1fc))
* **deps:** update alchemist to v37.1.4 ([e1b4c25](https://github.com/Collektive/collektive/commit/e1b4c257eba934b7a5d45883219c1e9582cac9f3))
* **deps:** update alchemist to v37.1.5 ([31c09d0](https://github.com/Collektive/collektive/commit/31c09d0a397dd5148ad3cb7820167a45bfa01152))
* **deps:** update dependency gradle to v8.12.1 ([ad4f01a](https://github.com/Collektive/collektive/commit/ad4f01a512281274a875c8eb2ca21c3988ece7ad))
* **deps:** update plugin com.gradle.develocity to v3.19.1 ([1e97342](https://github.com/Collektive/collektive/commit/1e97342ec996ca90625253dfd75925024481b85c))
* **deps:** update plugin multijvmtesting to v3.2.0 ([7c9ea13](https://github.com/Collektive/collektive/commit/7c9ea138d53371f973405ca86c0755885ca5bea9))
* **deps:** update plugin multijvmtesting to v3.2.1 ([1e7932f](https://github.com/Collektive/collektive/commit/1e7932fc06d8f0546dc9370974f2248a939768f7))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.19 ([e06695e](https://github.com/Collektive/collektive/commit/e06695ee68ad483df2f03ee26f779729c0e2a733))
* **deps:** update plugin publishoncentral to v8.0.2 ([87e6c51](https://github.com/Collektive/collektive/commit/87e6c5135cae5a2a60488514671af9a566c7b9e1))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.1 ([8a8c20f](https://github.com/Collektive/collektive/commit/8a8c20f91f3e11b6da352be7c8ef4165ce493808))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.2 ([c5739af](https://github.com/Collektive/collektive/commit/c5739af9e59dcc4a099d4ba084df48ad1d9e6070))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.3 ([e96c3bd](https://github.com/Collektive/collektive/commit/e96c3bd5a5070cf6de9f7b1af85a75e51fbf3e3c))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.5 ([b5778de](https://github.com/Collektive/collektive/commit/b5778de64a5ad4965631e6dd22b7ff6abe85ba97))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.6 ([8b273c6](https://github.com/Collektive/collektive/commit/8b273c605c97983169cd99be5b0dff57cf15696a))
* **deps:** update github/codeql-action action to v3.28.2 ([9ea1b09](https://github.com/Collektive/collektive/commit/9ea1b095984fceea928458e1bdf1196ff1255f9b))
* **deps:** update github/codeql-action action to v3.28.3 ([7b05a5e](https://github.com/Collektive/collektive/commit/7b05a5ebac4a39a45d756e97be25e1e665665b03))
* **deps:** update github/codeql-action action to v3.28.4 ([e1fd806](https://github.com/Collektive/collektive/commit/e1fd80682e9514f11972a4d9398693939785fe5c))
* **deps:** update github/codeql-action action to v3.28.5 ([8647549](https://github.com/Collektive/collektive/commit/8647549bbfb75c5fa6510f7e8c5ae6039f415d87))

## [12.3.0](https://github.com/Collektive/collektive/compare/12.2.2...12.3.0) (2025-01-20)

### Features

* **frontend-compiler-plugin:** check whether using a `YieldingContext` when not necessary ([#698](https://github.com/Collektive/collektive/issues/698)) ([95232f9](https://github.com/Collektive/collektive/commit/95232f9dd6d83d3d758469d2f05be8f514dc0209))

### Build and continuous integration

* prevent kotest classpath scan ([5befb2f](https://github.com/Collektive/collektive/commit/5befb2f607c367be736b81c7742f8e1cacbab4ed))
* use direct access syntax for source sets ([5f82f3f](https://github.com/Collektive/collektive/commit/5f82f3f00a956f9108220c00cdf6df07d508b7aa))

### Refactoring

* use jdk md5 hashing removing apache commons from dependencies ([9602772](https://github.com/Collektive/collektive/commit/960277242147da6637c49c6c394d6b437b0ade70))

## [12.2.2](https://github.com/Collektive/collektive/compare/12.2.1...12.2.2) (2025-01-18)

### Dependency updates

* **core-deps:** update plugin collektivize to v1.0.4 ([2f3d3e1](https://github.com/Collektive/collektive/commit/2f3d3e13f21f9344513656d0f061738f61f26ace))

## [12.2.1](https://github.com/Collektive/collektive/compare/12.2.0...12.2.1) (2025-01-18)

### Dependency updates

* **core-deps:** update plugin collektivize to v1.0.3 ([b68ba51](https://github.com/Collektive/collektive/commit/b68ba5132947b9b0c0e341b6e6ce7de24cf087e2))
* **deps:** update alchemist to v37 ([82ad709](https://github.com/Collektive/collektive/commit/82ad709c1542633ca78dc6e5dc95efb3b1e023ee))
* **deps:** update alchemist to v37.0.1 ([1faa052](https://github.com/Collektive/collektive/commit/1faa05218063c9496fd9050081145ef7cddf34c4))
* **deps:** update dependency com.github.ben-manes.caffeine:caffeine to v3.2.0 ([93f8476](https://github.com/Collektive/collektive/commit/93f847648d5dc7361dd3731c8c000d86fcb54704))

## [12.2.0](https://github.com/Collektive/collektive/compare/12.1.1...12.2.0) (2025-01-17)

### Features

* **frontend-compiler-plugin:** identify wrong use of pattern `evolve` + `neighboring` instead of using `share` ([#693](https://github.com/Collektive/collektive/issues/693)) ([fbb0337](https://github.com/Collektive/collektive/commit/fbb0337cdaee87a0857954268e975dbf5f78cf0b))

### Dependency updates

* **deps:** add collektivize gradle plugin ([22364c8](https://github.com/Collektive/collektive/commit/22364c80e1e2f895e458b25a065facdd0943b1cd))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.119 ([90195bd](https://github.com/Collektive/collektive/commit/90195bd99cc4b6ea68d636a327d0f1464e976d75))
* **deps:** update dependency typescript to v5.7.3 ([d41db26](https://github.com/Collektive/collektive/commit/d41db26efd474174e555024d7a3eb6d3e9296a94))

### Build and continuous integration

* **deps:** update actions/upload-artifact action to v4.6.0 ([8808143](https://github.com/Collektive/collektive/commit/8808143fa6fab0a2e7431b427e8a49fbffe1878f))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.7.0 ([d59005d](https://github.com/Collektive/collektive/commit/d59005d166245197514a3c35ad17fc10bb385ad0))
* **deps:** update github/codeql-action action to v3.28.1 ([fb4efc0](https://github.com/Collektive/collektive/commit/fb4efc06b6c9e194d6b8fd143122a4e1e343346e))
* generate collektivized stdlib via gradle plugin ([fcb0da7](https://github.com/Collektive/collektive/commit/fcb0da7fc912bf8920c0a45266dd3f5b6535db7a))

## [12.1.1](https://github.com/Collektive/collektive/compare/12.1.0...12.1.1) (2025-01-10)

### Bug Fixes

* recover the release of all the main artifacts ([a696227](https://github.com/Collektive/collektive/commit/a6962274d2572bb0486829fcf49042c3eeb62a37))

### Build and continuous integration

* don't run build on cancellation ([6714ba2](https://github.com/Collektive/collektive/commit/6714ba22489ffece27759fecb9c4a692bef535af))

## [12.1.0](https://github.com/Collektive/collektive/compare/12.0.5...12.1.0) (2025-01-10)

### Features

* intercept unnecessary use of aggregate constructs ([#668](https://github.com/Collektive/collektive/issues/668)) ([00f3d05](https://github.com/Collektive/collektive/commit/00f3d05c302f1ef12d1047692a06f1861253953f))

### Dependency updates

* **deps:** update alchemist ([6e0bae9](https://github.com/Collektive/collektive/commit/6e0bae99f7cf9a047c5ecf6f1bc8bc404705ea65))

### Build and continuous integration

* pin the version of ubuntu in the dispatcher workflow ([#688](https://github.com/Collektive/collektive/issues/688)) ([6d44278](https://github.com/Collektive/collektive/commit/6d44278404ec3fbc8e29991614f7bc05dc0b38e4))

## [12.0.5](https://github.com/Collektive/collektive/compare/12.0.4...12.0.5) (2025-01-09)

### Documentation

* enable dokka v2 ([#685](https://github.com/Collektive/collektive/issues/685)) ([18d758a](https://github.com/Collektive/collektive/commit/18d758a3c099cd3e667cac7c23be78760cf14474))

## [12.0.4](https://github.com/Collektive/collektive/compare/12.0.3...12.0.4) (2025-01-09)

### Dependency updates

* **deps:** update plugin publishoncentral to v8.0.1 ([45ea178](https://github.com/Collektive/collektive/commit/45ea178597fb47e475dfcc4f62e8aca448ba97aa))

### Bug Fixes

* release all gradle plugins on Maven Central ([ddedfe4](https://github.com/Collektive/collektive/commit/ddedfe45b2054d5df9156110cf27c3e20537cad0))

### Build and continuous integration

* **deps:** update danysk/action-create-ossrh-staging-repo action to v1.1.0 ([b281640](https://github.com/Collektive/collektive/commit/b281640f237bb8c1c97e5673a81798b7d0c2e70f))

## [12.0.3](https://github.com/Collektive/collektive/compare/12.0.2...12.0.3) (2025-01-09)

### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.118 ([bc01d18](https://github.com/Collektive/collektive/commit/bc01d18878dc83c7480601bb897e32a7fe244e86))
* **deps:** update node.js to 22.13 ([4291385](https://github.com/Collektive/collektive/commit/4291385203e87b636245136456be954c2b4eadbc))

### Bug Fixes

* use appropriate warning type preventing error inside Intellij `java.lang.NoClassDefFoundError: org/jetbrains/kotlin/com/intellij/psi/PsiElement` ([#684](https://github.com/Collektive/collektive/issues/684)) ([86ece5e](https://github.com/Collektive/collektive/commit/86ece5e40e071596881b68674d1e01d3d40dc7fc))

## [12.0.2](https://github.com/Collektive/collektive/compare/12.0.1...12.0.2) (2025-01-08)

### Dependency updates

* **core-deps:** update plugin kover to v0.9.1 ([3848660](https://github.com/Collektive/collektive/commit/3848660ce5d50df5419647c46affd155f2e097bb))
* **deps:** update docusaurus monorepo to v3.7.0 ([fc8a898](https://github.com/Collektive/collektive/commit/fc8a898ad5a9baf9f98c07f94550a4feb372128b))

### Build and continuous integration

* enable SARIF reports for ktlint ([#627](https://github.com/Collektive/collektive/issues/627)) ([ff660d3](https://github.com/Collektive/collektive/commit/ff660d3b696706063785da2b17159a8675e9dd64))
* merge sarif reports (detekt and ktlint) ([10dd234](https://github.com/Collektive/collektive/commit/10dd2340651eb7258f843e242bebc0f855a2d44f))

## [12.0.1](https://github.com/Collektive/collektive/compare/12.0.0...12.0.1) (2025-01-07)

### Dependency updates

* **core-deps:** update kotlin monorepo to v2.1.0 ([4514503](https://github.com/Collektive/collektive/commit/4514503a2828ea957c51bf52bd3461243c29e908))
* **deps:** update dependency commons-codec:commons-codec to v1.17.2 ([82108a8](https://github.com/Collektive/collektive/commit/82108a84b0d748b897a79708885d2916dd21c1fa))
* **deps:** update dependency io.github.freshmag:subjekt-api to v1.1.5 ([04f06d6](https://github.com/Collektive/collektive/commit/04f06d63f38783b555da44967170a2412d2ca0c1))

### Tests

* **dsl:** changed test with 'share' construct using the implicit parameter ([#674](https://github.com/Collektive/collektive/issues/674)) ([d270349](https://github.com/Collektive/collektive/commit/d27034912ffc21ec7c51bfc9385f04e60bdc4151))

### Build and continuous integration

* disable `allWarningsAsErrors` because of a bug in kotest https://github.com/kotest/kotest/pull/4598 ([98ab83d](https://github.com/Collektive/collektive/commit/98ab83d11ddab6a679fe72199d304ad2b3e9bee1))
* disable dokka 2 ([30e4083](https://github.com/Collektive/collektive/commit/30e40830ac299ce66bd5025cb122e83755f4020f))
* enable dokka 2.0.0 ([085b79c](https://github.com/Collektive/collektive/commit/085b79c50c26eb377cf7eadcfc6bdcd5899ad1b5))
* setup dokka with new logo ([2cc3b5d](https://github.com/Collektive/collektive/commit/2cc3b5d461236a14d33647e449de7ce305494211))
* use new API for setting compiler options ([1b2d262](https://github.com/Collektive/collektive/commit/1b2d262a7b1a5e7c5e1ad7b3db39320958c5ba68))

### General maintenance

* remove assets ([dcb022c](https://github.com/Collektive/collektive/commit/dcb022cbbe870178be76ee5e3a79a36012431ebb))

### Refactoring

* use kotlin 2.1.0 compiler api ([cd469ed](https://github.com/Collektive/collektive/commit/cd469ed0434fb3405295f8dd434911e4a42c9966))
* use new kotlin compiler api ([e631cfd](https://github.com/Collektive/collektive/commit/e631cfda68b22efc904b213de87377c91a82d8c2))

## [12.0.0](https://github.com/Collektive/collektive/compare/11.2.0...12.0.0) (2024-12-28)

### ⚠ BREAKING CHANGES

* **code-gen:** remove `plus` operators for `String` type since a more generic version in the kotlin stdlib shadows our generations (#620)

### Dependency updates

* **deps:** update plugin multijvmtesting to v3.1.1 ([f000068](https://github.com/Collektive/collektive/commit/f000068e1a8be3411c5ef43014c273ade3362b20))
* **deps:** update plugin multijvmtesting to v3.1.2 ([37bf7ad](https://github.com/Collektive/collektive/commit/37bf7ad04a3c424fb0c8c953f693c7687ec6e27d))

### Bug Fixes

* **code-gen:** remove `plus` operators for `String` type since a more generic version in the kotlin stdlib shadows our generations ([#620](https://github.com/Collektive/collektive/issues/620)) ([989649b](https://github.com/Collektive/collektive/commit/989649b88ecd421db7747bf67c16869ccf4e5280))

## [11.2.0](https://github.com/Collektive/collektive/compare/11.1.3...11.2.0) (2024-12-24)

### Features

* drop `com.tschuchort` library using new compiler embeddable library for testing the frontend compiler plugin ([#670](https://github.com/Collektive/collektive/issues/670)) ([74022bf](https://github.com/Collektive/collektive/commit/74022bfff4e86d9850d4a2ab02e653220e4d0a86))

### Dependency updates

* **deps:** update alchemist to v36.0.12 ([2efa4d6](https://github.com/Collektive/collektive/commit/2efa4d6f7b3ce5103a1d81676bb88c9fd3eb2e32))
* **deps:** update dependency gradle to v8.12 ([59608bf](https://github.com/Collektive/collektive/commit/59608bf791d23833d22edf0f2aa7d257e4c637c5))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.117 ([2cce4cc](https://github.com/Collektive/collektive/commit/2cce4ccb12a7168e9d68db0b3b4904c69be70474))
* **deps:** update plugin multijvmtesting to v3.0.2 ([b145993](https://github.com/Collektive/collektive/commit/b14599343b2df9f5661664ddf5e2b62a8919544a))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.18 ([83284ca](https://github.com/Collektive/collektive/commit/83284ca16c8038f199c96a07557502517eba3696))
* **deps:** update plugin publishoncentral to v7.0.3 ([96a8de3](https://github.com/Collektive/collektive/commit/96a8de3625af96da781cb1ae252a10e293f37dc3))
* **deps:** update plugin publishoncentral to v7.0.4 ([cfa2d26](https://github.com/Collektive/collektive/commit/cfa2d26ed72be20f012aa4c33049de3dc478c2c5))

### Build and continuous integration

* change deprecated methods and unused variables ([#666](https://github.com/Collektive/collektive/issues/666)) ([b528e14](https://github.com/Collektive/collektive/commit/b528e14b2ab204faee652261ffdfb7b55228168a))
* remove ps script ([b0661da](https://github.com/Collektive/collektive/commit/b0661daac51b699d4f822a5d6c847c145a72269c))

### Style improvements

* disable standard class signature for tests only ([1603348](https://github.com/Collektive/collektive/commit/160334810f739836abaa0de6aab9e4a40e8dca22))

## [11.1.3](https://github.com/Collektive/collektive/compare/11.1.2...11.1.3) (2024-12-20)

### Dependency updates

* **deps:** update alchemist to v36.0.11 ([6a600bd](https://github.com/Collektive/collektive/commit/6a600bd00fda954bda57c2fbf5a034bae738b55d))
* **deps:** update plugin publishoncentral to v7.0.2 ([6cdeb35](https://github.com/Collektive/collektive/commit/6cdeb354cf74a396b84591da28efd5011ad56205))

### Bug Fixes

* **build:** re-enabled publishing task for compiler embeddable sub-project ([#659](https://github.com/Collektive/collektive/issues/659)) ([9cdb6c2](https://github.com/Collektive/collektive/commit/9cdb6c2f1a0ac066c01b08b7040cd533776656df))

## [11.1.2](https://github.com/Collektive/collektive/compare/11.1.1...11.1.2) (2024-12-19)

### Dependency updates

* **core-deps:** update plugin kover to v0.9.0 ([da5014c](https://github.com/Collektive/collektive/commit/da5014c3acc866b7c84e0d75c8de1d33b55103c0))
* **deps:** update alchemist to v36.0.10 ([f9faf24](https://github.com/Collektive/collektive/commit/f9faf247cbd71899c1d55e0c01ac083965a4860e))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.28 ([cc122f3](https://github.com/Collektive/collektive/commit/cc122f3e2701ce7136aba70c6b6134e141dfa4ff))
* drop repository after the releasy always ([3fd6781](https://github.com/Collektive/collektive/commit/3fd67810d3f91f3b5306f548ffe17285e64d105a))

## [11.1.1](https://github.com/Collektive/collektive/compare/11.1.0...11.1.1) (2024-12-18)

### Bug Fixes

* solve a publication problem preventing the upload of compiler-plugin and gradle-plugin into the same staging repository ([a87ec30](https://github.com/Collektive/collektive/commit/a87ec3087db6de38745b428c73e5b88d466f578b))

## [11.1.0](https://github.com/Collektive/collektive/compare/11.0.2...11.1.0) (2024-12-18)

### Features

* improve checker functionality when `Aggregate` is used as a parameter ([#582](https://github.com/Collektive/collektive/issues/582)) ([323a756](https://github.com/Collektive/collektive/commit/323a756a9a424b2b79b5a57f16e2d534c6fee252))

### Dependency updates

* **deps:** update alchemist to v36.0.4 ([47f9bd7](https://github.com/Collektive/collektive/commit/47f9bd7ef47d3911b849547f54e11f42636705cb))
* **deps:** update alchemist to v36.0.5 ([b38539b](https://github.com/Collektive/collektive/commit/b38539b7e70b306211b3127ec329cb37ac03aa2b))
* **deps:** update alchemist to v36.0.6 ([2303b53](https://github.com/Collektive/collektive/commit/2303b53bd84ebf6bb54cf8bd18c6905f229ca117))
* **deps:** update alchemist to v36.0.7 ([1a0f96d](https://github.com/Collektive/collektive/commit/1a0f96dde2284fc6079a412dda7ec0295eaeaecd))
* **deps:** update alchemist to v36.0.9 ([4aafea7](https://github.com/Collektive/collektive/commit/4aafea7f2514b671f28d9e8ba06bf75b411e3846))
* **deps:** update dependency org.apache.commons:commons-text to v1.13.0 ([7abc326](https://github.com/Collektive/collektive/commit/7abc3266dfd757f660e7ff213432f24bf5db0bdb))
* **deps:** update dependency org.jetbrains.dokka to v2 ([c81d71d](https://github.com/Collektive/collektive/commit/c81d71d7e1626defdf595bfedf3caa7be56b056e))
* **deps:** update dependency prism-react-renderer to v2.4.1 ([6a76c61](https://github.com/Collektive/collektive/commit/6a76c6184f18efbe5c791302a0ccaed3481da4d4))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.116 ([dde7590](https://github.com/Collektive/collektive/commit/dde75903178ff1aa8f0b859b592c021df84bb914))
* **deps:** update node.js to 22.12 ([90c731e](https://github.com/Collektive/collektive/commit/90c731ebe9eeb1cd271fe39f029ebc2b4927e3b0))
* **deps:** update plugin com.gradle.develocity to v3.19 ([a84b652](https://github.com/Collektive/collektive/commit/a84b652de6265baa069e5f15d9eb0319c467697e))
* **deps:** update plugin kotlin-qa to v0.78.0 ([8d155e5](https://github.com/Collektive/collektive/commit/8d155e5d8473a061fc8bcd64ce2fe87d18be6dab))
* **deps:** update plugin multijvmtesting to v2 ([1bbd415](https://github.com/Collektive/collektive/commit/1bbd415458556734062c5d9e65474846e4b74cae))
* **deps:** update plugin multijvmtesting to v2.0.1 ([1cc5ad9](https://github.com/Collektive/collektive/commit/1cc5ad986e2a2e0b5b16370ccc20ea9314dd7070))
* **deps:** update plugin multijvmtesting to v3 ([f1df575](https://github.com/Collektive/collektive/commit/f1df5755a0dc8e22d05e29ae1c17d4f57132c861))
* **deps:** update plugin multijvmtesting to v3.0.1 ([8f9a9bd](https://github.com/Collektive/collektive/commit/8f9a9bd9ead6dfe4a8c8d2b3e1b4f190ec3d01db))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.16 ([793e4b1](https://github.com/Collektive/collektive/commit/793e4b1b3e3afce262e18a901c269758ee62ef7d))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.17 ([4e56dcb](https://github.com/Collektive/collektive/commit/4e56dcbf46e361ea135583a76fc026e1c2ae10d4))
* **deps:** update plugin publishoncentral to v6 ([0944a0b](https://github.com/Collektive/collektive/commit/0944a0bc8412a24eb72bfe60c5e782eace87d8a0))
* **deps:** update plugin publishoncentral to v6.0.1 ([a3a230e](https://github.com/Collektive/collektive/commit/a3a230edb20b7ffddbbeead814fa179813d28c4a))
* **deps:** update plugin publishoncentral to v7 ([c7be422](https://github.com/Collektive/collektive/commit/c7be4227c7af49d8266aec6264d143870a7b4573))
* **deps:** update plugin publishoncentral to v7.0.1 ([ff9cf11](https://github.com/Collektive/collektive/commit/ff9cf11613c830ba11ab5fd90ed938a2eb4e6e92))

### Build and continuous integration

* **deps:** bump cross-spawn from 7.0.3 to 7.0.5 in /site ([#589](https://github.com/Collektive/collektive/issues/589)) ([f15653d](https://github.com/Collektive/collektive/commit/f15653d4559d318d3a1b8032a66718788795e898))
* **deps:** update actions/upload-artifact action to v4.5.0 ([a47b83f](https://github.com/Collektive/collektive/commit/a47b83f38543b2535db205dc1ba47b01d64ac5e9))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.24 ([31e3060](https://github.com/Collektive/collektive/commit/31e30603b4ff819eb5ef0a877b7a290b756ca4f3))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.25 ([66195cf](https://github.com/Collektive/collektive/commit/66195cf8742a6229cca4f44d64df195f6e354875))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.26 ([3371402](https://github.com/Collektive/collektive/commit/33714028869ec9bb90078e0ba33a4acb6725e4e6))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.27 ([b5de070](https://github.com/Collektive/collektive/commit/b5de07026251554187b18b01f4949767a2172ad3))
* do not restore stagin repo ids in PRs ([01d77b1](https://github.com/Collektive/collektive/commit/01d77b15461238c8e95e1ce3845301543cc9477a))
* enable caching and incremental compilation for speedup the build process ([ec08b9d](https://github.com/Collektive/collektive/commit/ec08b9d393e722cec4eca40a9a4fa5db4b365c77))
* enable SARIF reports upload ([#626](https://github.com/Collektive/collektive/issues/626)) ([b263637](https://github.com/Collektive/collektive/commit/b263637b29ae63173732d292292014fd9fb5cd92))
* optimized build leveraging macos runner for publishing ([#639](https://github.com/Collektive/collektive/issues/639)) ([36b6330](https://github.com/Collektive/collektive/commit/36b633092e1662aeaee06338404238a0cd439168))
* override check command to upload code coverage ([4ea18c8](https://github.com/Collektive/collektive/commit/4ea18c81f6581d7337f9030b77a11e6491c781ef))
* prevent supertypes on new line formatting (ktlint) ([fb06406](https://github.com/Collektive/collektive/commit/fb064067f34a8f647f02fdbadb2e7152e59aefc3))
* remove uneeded drop since it will be performed always at the end ([5a33374](https://github.com/Collektive/collektive/commit/5a333746aa317575cb0c2950759b8e05458c0037))
* set kover path explicit for codecov ([6c85264](https://github.com/Collektive/collektive/commit/6c852645a2a4d7ab2bec0c16be36c83c79a22dfc))
* success depends also on website build ([ba4a7b6](https://github.com/Collektive/collektive/commit/ba4a7b62965b161b01272719561c9fca1868a1a7))
* success task depend on build-website ([7d341f2](https://github.com/Collektive/collektive/commit/7d341f2e9e1a60346aa00e3047a5c05e7df187fa))
* upload also gradle plugin and compiler plugin ([b8a4852](https://github.com/Collektive/collektive/commit/b8a4852f1483abc821d8bca480891f721359f1e9))
* use curl for last step closing repository ([f3ba554](https://github.com/Collektive/collektive/commit/f3ba554c806f0dec5d84db1ebf40fb48f4fa3060))

### Style improvements

* new formatting according to new styling rules ([760ef8a](https://github.com/Collektive/collektive/commit/760ef8a7400bcdf49ad72471146f54bf41b64d0b))
* new formatting according to new styling rules ([2bd2bb8](https://github.com/Collektive/collektive/commit/2bd2bb8d5412a985d19f57c4c398b76eafe0cc78))

## [11.0.2](https://github.com/Collektive/collektive/compare/11.0.1...11.0.2) (2024-12-02)

### Dependency updates

* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.9.0 ([95a2724](https://github.com/Collektive/collektive/commit/95a2724547620b5151322d2f0d6fe8def5d9efe6))

### Bug Fixes

* **stdlib:** non-self-stabilizing gossip implemented with fold instead of hood ([#579](https://github.com/Collektive/collektive/issues/579)) ([3de2401](https://github.com/Collektive/collektive/commit/3de24014235dad3b4f2de6697c37428e4ff2e550))

## [11.0.1](https://github.com/Collektive/collektive/compare/11.0.0...11.0.1) (2024-11-30)

### Dependency updates

* **deps:** update alchemist to v35.0.1 ([72ff9bc](https://github.com/Collektive/collektive/commit/72ff9bcf72ff184b55338458a428815bc61cfe45))
* **deps:** update alchemist to v35.0.2 ([636e97e](https://github.com/Collektive/collektive/commit/636e97ebaf08513ec59aeec5b5c5713c14bfe86a))
* **deps:** update alchemist to v35.0.3 ([0cabf44](https://github.com/Collektive/collektive/commit/0cabf4463ca228c6a5a9e83b5e43b37815d43e40))
* **deps:** update alchemist to v36 ([2998725](https://github.com/Collektive/collektive/commit/2998725a7b7b5b9ff3c792bbd8de098ed3a12eeb))
* **deps:** update alchemist to v36.0.1 ([c8282c4](https://github.com/Collektive/collektive/commit/c8282c4f99ebfcf7be5a27cbf5b527a8bc314fa5))
* **deps:** update alchemist to v36.0.2 ([8aea0ff](https://github.com/Collektive/collektive/commit/8aea0ffff752776410666493ce55d6f26c00d606))
* **deps:** update alchemist to v36.0.3 ([dc3d28e](https://github.com/Collektive/collektive/commit/dc3d28efba1e59166ac703d034ac0666bc187501))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.5.1 ([92e1bd6](https://github.com/Collektive/collektive/commit/92e1bd640ba1c8a42bc14027149837ebacb549fb))
* **deps:** update dependency gradle to v8.11 ([6635b68](https://github.com/Collektive/collektive/commit/6635b682340f71f510767f426b1ba42ebd1e1012))
* **deps:** update dependency gradle to v8.11.1 ([3c3104c](https://github.com/Collektive/collektive/commit/3c3104cccaa9864b09ba9128bf593215b090f4f1))
* **deps:** update dependency typescript to ~5.7.0 ([c27b791](https://github.com/Collektive/collektive/commit/c27b791857657df7ba0725a1cb75d485728ca95f))
* **deps:** update docusaurus monorepo to v3.6.2 ([0ec0b9b](https://github.com/Collektive/collektive/commit/0ec0b9bca33c2d2afed50d0740e795051f99b756))
* **deps:** update docusaurus monorepo to v3.6.3 ([dbbf757](https://github.com/Collektive/collektive/commit/dbbf75728213f50ed882469bc4987898b06899ba))
* **deps:** update plugin kotlin-qa to v0.70.0 ([d26972d](https://github.com/Collektive/collektive/commit/d26972d61cdc35e29bc5eed25fea3dbaef8611ae))
* **deps:** update plugin kotlin-qa to v0.70.1 ([c09c666](https://github.com/Collektive/collektive/commit/c09c666c96c566c54edc38242d21bb829c339e9b))
* **deps:** update plugin kotlin-qa to v0.70.2 ([a1e533e](https://github.com/Collektive/collektive/commit/a1e533e73f8e8d957401da1ea32c0222693ddf19))
* **deps:** update plugin kotlin-qa to v0.74.0 ([3b0e1a0](https://github.com/Collektive/collektive/commit/3b0e1a02220e1e7c8cf74b07b22917c07afa529d))
* **deps:** update plugin kotlin-qa to v0.75.0 ([dc1911b](https://github.com/Collektive/collektive/commit/dc1911bb8851edf292368c8098a552163602244d))
* **deps:** update plugin multijvmtesting to v1.3.1 ([bb08140](https://github.com/Collektive/collektive/commit/bb081404a271d5bf78b29f25062bbb7a75588f51))
* **deps:** update plugin multijvmtesting to v1.3.2 ([abd3512](https://github.com/Collektive/collektive/commit/abd3512bc0803ca10db95283972f1d7399137aaa))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.14 ([1486f09](https://github.com/Collektive/collektive/commit/1486f09095d08e94f2a467ac8a200f18c8ce4749))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.15 ([0f611b0](https://github.com/Collektive/collektive/commit/0f611b032eaa3bd3d9e22cf3f921c3710808d8df))
* **deps:** update plugin publishoncentral to v5.1.10 ([830240f](https://github.com/Collektive/collektive/commit/830240f6f6441cb598701d09628683fad60add6f))
* **deps:** update plugin publishoncentral to v5.1.11 ([08f1df4](https://github.com/Collektive/collektive/commit/08f1df4887a3e539913d86065f32ebc4178ab402))

### Bug Fixes

* fix bug with yielding-based functions causing a ClassCastException ([#616](https://github.com/Collektive/collektive/issues/616)) ([39cd80a](https://github.com/Collektive/collektive/commit/39cd80ad2a0cd53f8c5e7adc56ced251c6cb3223))

### Build and continuous integration

* add a forced cleanup job to keep OSSRH clean ([bca17dc](https://github.com/Collektive/collektive/commit/bca17dc1c0d5c0d31c35c1c5936f6dae0b93816e))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.15 ([9ec3864](https://github.com/Collektive/collektive/commit/9ec3864d2976050f3d8d7653cf9721e047e79dbd))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.16 ([373b0d2](https://github.com/Collektive/collektive/commit/373b0d22eacab53d0166c1228a02b4ec44941327))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.17 ([d8c3e17](https://github.com/Collektive/collektive/commit/d8c3e17566cac5d13e69575da18d39925dc3b114))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.18 ([95fc1c7](https://github.com/Collektive/collektive/commit/95fc1c76f2313f955165af2eff027586ca97d490))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.19 ([c903733](https://github.com/Collektive/collektive/commit/c903733c069b02c3c1ffecf9410fbbce076a1f2d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.20 ([799e74d](https://github.com/Collektive/collektive/commit/799e74d957dac6c7d8866704cf72fe3c906e3060))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.21 ([4e158cb](https://github.com/Collektive/collektive/commit/4e158cb054bb6a3dee87c6aa83b1a2f822d5163e))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.22 ([715993c](https://github.com/Collektive/collektive/commit/715993c4b34e41eadf427bd1743d02ba7974ce42))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.23 ([ed915ad](https://github.com/Collektive/collektive/commit/ed915ad07602bb4181fcc60c5674de8b8f52ff89))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.23 ([f969aa8](https://github.com/Collektive/collektive/commit/f969aa8fd4e05a928bbc607a895f22094642177a))

### Style improvements

* reformat style according to new style configuration ([142aa89](https://github.com/Collektive/collektive/commit/142aa894d46f1337619fa05ea4ff25babf07beb9))
* reformat style according to new style configuration ([dcfb568](https://github.com/Collektive/collektive/commit/dcfb5683c6f5c7678151dcdbcf57ee25afc04a75))

### Refactoring

* move warnings and error in appropriate objects following the jetbrains convention ([#578](https://github.com/Collektive/collektive/issues/578)) ([a8ffc16](https://github.com/Collektive/collektive/commit/a8ffc1658ecb2d539cce437592f05875e7e8f2e9))

## [11.0.0](https://github.com/Collektive/collektive/compare/10.11.1...11.0.0) (2024-11-14)

### ⚠ BREAKING CHANGES

* **dsl:** rename repeat/repeating functions into evolve/evolving (#577)

### Refactoring

* **dsl:** rename repeat/repeating functions into evolve/evolving ([#577](https://github.com/Collektive/collektive/issues/577)) ([b616ddb](https://github.com/Collektive/collektive/commit/b616ddbc2a1356a7277f2472559cfe40c3a778c5))

## [10.11.1](https://github.com/Collektive/collektive/compare/10.11.0...10.11.1) (2024-11-14)

### Dependency updates

* **deps:** update alchemist to v35 ([97f03c9](https://github.com/Collektive/collektive/commit/97f03c9630c73cec754cd2d3974089d596a0a056))
* **deps:** update dependency dev.zacsweers.kctfork:core to v0.6.0 ([8952271](https://github.com/Collektive/collektive/commit/89522714accbc8e8f2fe1c7905b6c0faedd8e2f9))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.115 ([a01b8d6](https://github.com/Collektive/collektive/commit/a01b8d643c04b16c8a39e79c61a2a3f7f668c3cf))
* **deps:** update docusaurus monorepo to v3.6.1 ([4a6015d](https://github.com/Collektive/collektive/commit/4a6015da313b0afb8bbc7e8d547f7f9e032e528f))
* **deps:** update plugin com.gradle.develocity to v3.18.2 ([1bc080b](https://github.com/Collektive/collektive/commit/1bc080ba9a2baacc16b870cd71c89243664d660c))

### Bug Fixes

* **dsl:** add regression test for Path cache IllegalStateException ([#567](https://github.com/Collektive/collektive/issues/567)) ([dae88e2](https://github.com/Collektive/collektive/commit/dae88e23c0383f76dcba7aad1c1869f3bced7fe6))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.13 ([28c0929](https://github.com/Collektive/collektive/commit/28c0929d51b169389e78f37f589c7be73702c3a9))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.14 ([f49c78f](https://github.com/Collektive/collektive/commit/f49c78fa53da8bb21fd9af6e57360bbc1c6f6d02))
* move stop gradle on windows before the deploy step preventing failing silently the CI ([8e1f0e4](https://github.com/Collektive/collektive/commit/8e1f0e4d4908ebecb98bbd0b0ee0692e5913a690))

### Style improvements

* explicit name in outer lambda ([3502860](https://github.com/Collektive/collektive/commit/3502860e396cfe00064f03f206847558882b6142))
* reformat file according to ktlin ([3481efd](https://github.com/Collektive/collektive/commit/3481efdd027081a5f4c23648aa53a1967e909a6a))

## [10.11.0](https://github.com/Collektive/collektive/compare/10.10.0...10.11.0) (2024-11-11)

### Features

* **field:** change `toString` representation for `Field` ([#558](https://github.com/Collektive/collektive/issues/558)) ([15b37a9](https://github.com/Collektive/collektive/commit/15b37a93a3d5a4ffcf47dbcd4bfe7df175ca7cc9))

## [10.10.0](https://github.com/Collektive/collektive/compare/10.9.0...10.10.0) (2024-11-10)

### Features

* **field:** add `alignedMapWithId` for consistency with `*WithId` functions ([#554](https://github.com/Collektive/collektive/issues/554)) ([201f1bf](https://github.com/Collektive/collektive/commit/201f1bf70b9f67267b979875843c6c9bfe8042da))

### Dependency updates

* **deps:** update alchemist to v34.1.13 ([92e45ef](https://github.com/Collektive/collektive/commit/92e45ef04c7e8a9626b61903c3a4640b15fc2ac6))
* **deps:** update alchemist to v34.1.14 ([9906d63](https://github.com/Collektive/collektive/commit/9906d6398c98c9c8ab1c78f5b12f6e5725c09a86))
* **deps:** update plugin multijvmtesting to v1.3.0 ([3f80fac](https://github.com/Collektive/collektive/commit/3f80fac61f0f3318f45dd1bb8ebb547b839f4c21))

## [10.9.0](https://github.com/Collektive/collektive/compare/10.8.0...10.9.0) (2024-11-08)

### Features

* **compiler-plugin:** new checker for collections' methods, refactoring and new testing utility ([#508](https://github.com/Collektive/collektive/issues/508)) ([d9cd8b3](https://github.com/Collektive/collektive/commit/d9cd8b3e8a2582972415ad9dad76c26a90abad7c)), closes [#489](https://github.com/Collektive/collektive/issues/489)

## [10.8.0](https://github.com/Collektive/collektive/compare/10.7.0...10.8.0) (2024-11-07)

### Features

* **field:** add `replaceMatching` method replacing the values in a field matching a given predicate ([#550](https://github.com/Collektive/collektive/issues/550)) ([ca768d0](https://github.com/Collektive/collektive/commit/ca768d0c6e9182893ec4d4a94bea9049da306241))

### Dependency updates

* **deps:** update alchemist to v34.1.12 ([81f4d20](https://github.com/Collektive/collektive/commit/81f4d20f928320eb5abdf889cc25c591ae2b600e))
* **deps:** update docusaurus monorepo to v3.6.0 ([c7e9243](https://github.com/Collektive/collektive/commit/c7e92432ed9dff67a2c3d5acc31aa997aeb24a7e))

### General maintenance

* add basic codecov configuration ([1f4a8c9](https://github.com/Collektive/collektive/commit/1f4a8c98380488c2d50b7deba59544d0bec93ec0))

## [10.7.0](https://github.com/Collektive/collektive/compare/10.6.0...10.7.0) (2024-11-06)

### Features

* add aggregate gossip algorithm ([85e9a70](https://github.com/Collektive/collektive/commit/85e9a70e7b4183397b9f8231142d1751b0f859c1))
* add ever happened gossip algorithm ([16f9439](https://github.com/Collektive/collektive/commit/16f94390439950974d081fc4ec77e1dc48432d93))
* add gossipMax and Min with default comparator ([e4fd957](https://github.com/Collektive/collektive/commit/e4fd957c94ac6185935eafc7299e3ef783cf7dd8))
* add non-self-stabilizing gossip and gossip ever happened ([3140aa4](https://github.com/Collektive/collektive/commit/3140aa409a535ff633474d2e23a5413fd7a731c6))

### Dependency updates

* **deps:** update alchemist to v34.1.10 ([17dc503](https://github.com/Collektive/collektive/commit/17dc503ad975d2fa86bc6215eb2b98113f06ecea))
* **deps:** update alchemist to v34.1.11 ([bef740d](https://github.com/Collektive/collektive/commit/bef740db5b695e64cd368eeb804f1b12483f587c))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.113 ([f0f60a2](https://github.com/Collektive/collektive/commit/f0f60a28205d0314d4e726411c9889c15e431317))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.114 ([b577726](https://github.com/Collektive/collektive/commit/b577726628ad8ff15cca0508c512ebaa998c02e8))
* **deps:** update node.js to v22 ([c37ef8a](https://github.com/Collektive/collektive/commit/c37ef8a51962acd920b70765cb36d46cdfa0e5f2))

### Documentation

* add doc to gossip objects ([f140ad6](https://github.com/Collektive/collektive/commit/f140ad6af705cd255e7b0fcb1e974712bbdd47c0))
* minor in gossipMin doc ([423f2dd](https://github.com/Collektive/collektive/commit/423f2ddd1885b6dce405f8369e90d7ecc211da6a))

### Performance improvements

* improve algorithm by checking the presence of more than one neighbor in the incoming path ([d37508a](https://github.com/Collektive/collektive/commit/d37508aa30adf414739bc6aeffd8d20beddb39dc))

### Tests

* add check on result of the computation ([636a4fc](https://github.com/Collektive/collektive/commit/636a4fcd309a506cf70a0adef20c6dfec5de0adf))
* add test for gossipMin ([93b8652](https://github.com/Collektive/collektive/commit/93b8652c45a4794e7bc5ccc76d556e20205ca57b))
* add test for stabilizing gossip ([39562f2](https://github.com/Collektive/collektive/commit/39562f2cd7a1906f0289d716588f3f21c239256d))
* refactor gossipMax function as in stdlib ([d87ac21](https://github.com/Collektive/collektive/commit/d87ac210be17183733b8990173094883b2349fef))

### Build and continuous integration

* **deps:** update nicolasfara/precompute-semantic-release-version-action action to v2.0.2 ([c0de79b](https://github.com/Collektive/collektive/commit/c0de79b48ddaafcfe4b73e7bd525fc72d1aba041))
* **renovate:**  use `with` when importing the configuration instead of `assert` ([dc63d13](https://github.com/Collektive/collektive/commit/dc63d1376175dfd5475c080db0652adc8f901cb5))

### General maintenance

* remove useless spacing ([cbefd9c](https://github.com/Collektive/collektive/commit/cbefd9cd1426772750fa3de5836a76dbe5e4c4a2))

### Style improvements

* unify generic type and change function indentation ([60ee490](https://github.com/Collektive/collektive/commit/60ee490efb3eaca3e0412b98c3ba626b70645302))

### Refactoring

* function names ([eb76a21](https://github.com/Collektive/collektive/commit/eb76a213f5cd36412d4819bef097e93c5d10d411))
* keep track of neighbor's initial value to avoid losing rounds ([6882524](https://github.com/Collektive/collektive/commit/68825241e4bb9ca573975847185efb480c517fd3))
* minors on val names and docs ([5f58744](https://github.com/Collektive/collektive/commit/5f58744399c3cae622e8eb517fc16d5e46914f71))
* more idiomatic implementation ([45f518e](https://github.com/Collektive/collektive/commit/45f518edf67534896c4019c9248c2e5bfad2cc54))
* rename function and generic types ([dad4673](https://github.com/Collektive/collektive/commit/dad46739112aceeb068bdf24c3a9cc9038f921c1))
* rename function name as [@danysk](https://github.com/danysk) suggested ([9ce71c7](https://github.com/Collektive/collektive/commit/9ce71c74f67d2f35272a2f743025a330531829e5))
* use foldWithID inside gossip function ([e30454c](https://github.com/Collektive/collektive/commit/e30454c88cbed6c8f9ee0f3f8924f0dbfeb0b3cd))

## [10.6.0](https://github.com/Collektive/collektive/compare/10.5.3...10.6.0) (2024-10-31)

### Features

* **fields:** add hood and fold with IDs ([#536](https://github.com/Collektive/collektive/issues/536)) ([d60caa8](https://github.com/Collektive/collektive/commit/d60caa8a331dd2f2a243db9368017597029c882f))

### Build and continuous integration

* remove generated stdlib from coverage ([dceb268](https://github.com/Collektive/collektive/commit/dceb26837eb47e76fbc6e07cf8ff99818ff6df99))
* set codecov folder pointing to kover output reports ([d9aade6](https://github.com/Collektive/collektive/commit/d9aade6e88b6ee25122e102ccde9214aa8ff1049))

## [10.5.3](https://github.com/Collektive/collektive/compare/10.5.2...10.5.3) (2024-10-28)

### Dependency updates

* **core-deps:** update kotlin monorepo to v2.0.21 ([4917030](https://github.com/Collektive/collektive/commit/49170309c5c21eea84290bdd2280e077185c94d8))
* **deps:** update alchemist to v34.1.9 ([7e04f77](https://github.com/Collektive/collektive/commit/7e04f77f67942c3bcdfac246b498b4d28f20a49f))
* **deps:** update dependency com.squareup:kotlinpoet to v2 ([27ea520](https://github.com/Collektive/collektive/commit/27ea520f0ac72eb3f355bce7f6162d966beecfb3))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.112 ([9afaa36](https://github.com/Collektive/collektive/commit/9afaa36d5d6eda344f51a5d79177e52ca90c9257))
* **deps:** update plugin kotlin-qa to v0.69.0 ([2112b43](https://github.com/Collektive/collektive/commit/2112b436b956c44c7d9f8920792271c38a070b16))

### Build and continuous integration

* **deps:** update actions/checkout action to v4.2.2 ([45a9e38](https://github.com/Collektive/collektive/commit/45a9e38f0ba981d5d61c1e6188fffcf1f059c41d))
* **deps:** update actions/setup-node action to v4.1.0 ([93acd3b](https://github.com/Collektive/collektive/commit/93acd3bae3dc624e3568810c9a79d705ea3d571a))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.12 ([6fdb751](https://github.com/Collektive/collektive/commit/6fdb751c1fa85e1b2e972c451085004c8a60e542))
* fail fast during the release job ([f9c9e3b](https://github.com/Collektive/collektive/commit/f9c9e3b801047361ec9c8c4e5951267d1cc22bc7))
* fix git status on documentation repository ([12d0f32](https://github.com/Collektive/collektive/commit/12d0f3256ff8e0a52e8ce9c4430dd8f40d616486))
* properly manage site pubblication when no changes to the website are performed but a release is required ([e158bf4](https://github.com/Collektive/collektive/commit/e158bf40634fd16344fc1b057dd781de4f26697a))
* use organisation secret ([45ec9d4](https://github.com/Collektive/collektive/commit/45ec9d41f40177ff3e081c28c669ba54ee9db636))

### Style improvements

* remove warning ([58805a4](https://github.com/Collektive/collektive/commit/58805a4c8392e8df8913633ff05730e6f293cf0e))

## [10.5.2](https://github.com/Collektive/collektive/compare/10.5.1...10.5.2) (2024-10-22)

### Dependency updates

* **deps:** update alchemist to v34.1.8 ([198c13c](https://github.com/Collektive/collektive/commit/198c13c388758157f16acbcb4aea59fa200952e9))

### Documentation

* **site:** update algolia api pointing to unlimited account ([1ed91ec](https://github.com/Collektive/collektive/commit/1ed91ec7cb05623c39998712d71a0b24c56572bc))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.11 ([041b0ff](https://github.com/Collektive/collektive/commit/041b0ffeded04beefa029956eb5ec77a4584a369))

## [10.5.1](https://github.com/Collektive/collektive/compare/10.5.0...10.5.1) (2024-10-22)

### Dependency updates

* **deps:** update dependency it.unibo.alchemist:alchemist-api to v34.1.7 ([0487e63](https://github.com/Collektive/collektive/commit/0487e63506f662a8228be087d16b7f5914815acc))

### Documentation

* **site:** setup getting started page and enable algola search ([0f8089d](https://github.com/Collektive/collektive/commit/0f8089d7645f01a2f214ebff24f4a89537922a80))

## [10.5.0](https://github.com/Collektive/collektive/compare/10.4.0...10.5.0) (2024-10-21)

### Dependency updates

* **api-deps:** update kotlin to v2.0.20 and Kotest to v6.0.0.M1 ([#518](https://github.com/Collektive/collektive/issues/518)) ([629076b](https://github.com/Collektive/collektive/commit/629076b5bfb239f4f4ad2d23988f21dfc3351c1a))
* **deps:** update alchemist to v34.1.6 ([6653b67](https://github.com/Collektive/collektive/commit/6653b67425564d19ac2349bf2a464df552554091))
* **deps:** update dependency @mdx-js/react to v3.1.0 ([998bc35](https://github.com/Collektive/collektive/commit/998bc3539ad8f2ee4acff5785897b7769889747c))

### Build and continuous integration

* use the built-in feature of setup-node to install the correct node engine version ([#519](https://github.com/Collektive/collektive/issues/519)) ([3fdfa8b](https://github.com/Collektive/collektive/commit/3fdfa8b4a2d93b7f33bc6b1c033e46cffa2219bc))

### General maintenance

* **release:** 10.5.0 [skip ci] ([42b3f4b](https://github.com/Collektive/collektive/commit/42b3f4be23b08cbc8fa411b476141e173c49ca11)), closes [#518](https://github.com/Collektive/collektive/issues/518)

## [10.5.0](https://github.com/Collektive/collektive/compare/10.4.0...10.5.0) (2024-10-20)

### Dependency updates

* **api-deps:** update kotlin to v2.0.20 and Kotest to v6.0.0.M1 ([#518](https://github.com/Collektive/collektive/issues/518)) ([629076b](https://github.com/Collektive/collektive/commit/629076b5bfb239f4f4ad2d23988f21dfc3351c1a))

## [10.4.0](https://github.com/Collektive/collektive/compare/10.3.2...10.4.0) (2024-10-19)

### Features

* create site for documentation ([f135443](https://github.com/Collektive/collektive/commit/f1354437ffac517d25f5dbfc7fb5de840680cedf))

### Dependency updates

* **deps:** update alchemist to v34.1.5 ([ae2ff42](https://github.com/Collektive/collektive/commit/ae2ff4260ab45d1171b72b7bc2b9d5ce32f791a6))
* **deps:** update dependency gradle to v8.10.2 ([ddb55c2](https://github.com/Collektive/collektive/commit/ddb55c2eb17c1ef4657e9af560585af168a28cbe))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.107 ([1e7f015](https://github.com/Collektive/collektive/commit/1e7f01518e010feeb6654c871429f88e94d90f51))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.108 ([b7dc684](https://github.com/Collektive/collektive/commit/b7dc684438d53ce65b2bda2fd440b27aa4a6a590))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.109 ([657e09a](https://github.com/Collektive/collektive/commit/657e09a35f1b5539a9c6d33aa12e9fd628327f4d))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.110 ([9084d68](https://github.com/Collektive/collektive/commit/9084d68c93f97c214554cb7fe069f9f42704f1ce))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.111 ([aa03d44](https://github.com/Collektive/collektive/commit/aa03d4417cc51f765c4ce657f2a538a5251b26d0))
* **deps:** update dependency typescript to ~5.6.0 ([6a936b6](https://github.com/Collektive/collektive/commit/6a936b635c59c487118362d35413eff1a812293b))
* **deps:** update node.js to 20.18 ([70d78ef](https://github.com/Collektive/collektive/commit/70d78ef761e186f1e2af45af7c34774a16c231c6))
* **deps:** update plugin kotlin-qa to v0.67.2 ([d8dee31](https://github.com/Collektive/collektive/commit/d8dee31c72d68b00206720ee49346eb60778663d))
* **deps:** update plugin kotlin-qa to v0.67.3 ([4415f10](https://github.com/Collektive/collektive/commit/4415f1058f6efd9ce79ca1f72df426c872453ec8))
* **deps:** update plugin kotlin-qa to v0.68.0 ([b4fce68](https://github.com/Collektive/collektive/commit/b4fce68628f9860e931d25d36aa31f904a53d5a0))
* **deps:** update plugin multijvmtesting to v1.2.8 ([7d5985b](https://github.com/Collektive/collektive/commit/7d5985b06285ecfac4634e811d4561d736d72ef4))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.13 ([e8fb01d](https://github.com/Collektive/collektive/commit/e8fb01db30e6918b79e22ff424f44350bc9fdef4))
* **deps:** update plugin publishoncentral to v5.1.8 ([8a7bd97](https://github.com/Collektive/collektive/commit/8a7bd97f0bfdd9c8acafcada0aefeb432fda6a5f))
* **deps:** update plugin publishoncentral to v5.1.9 ([a9ddce6](https://github.com/Collektive/collektive/commit/a9ddce6a576234f7b82acbcb40aa12358e6882fa))

### Documentation

* **site:** revam frontpage ([2f1716b](https://github.com/Collektive/collektive/commit/2f1716b76faaa4074aca73a8a68ba0c62ae17e9e))
* **website:** solved security warning ([80d149b](https://github.com/Collektive/collektive/commit/80d149b9df777a82500a79fe8d544cfe52fa1752))

### Tests

* using the same name convention for all test classes ([#489](https://github.com/Collektive/collektive/issues/489)) ([f72a900](https://github.com/Collektive/collektive/commit/f72a9009ac8aa00c1bbf1e3f8423c10b7666eba8))

### Build and continuous integration

* add command for website deployment ([5377d76](https://github.com/Collektive/collektive/commit/5377d76190a0ce5723b85a79ed3f34b2bfb5feca))
* **deps:** remove arrow dependency ([e5bb75b](https://github.com/Collektive/collektive/commit/e5bb75b8d8c1cf76d9d88622d542917eca88c84f))
* **deps:** update actions/checkout action to v4 ([947fbf4](https://github.com/Collektive/collektive/commit/947fbf4b74e694fccf61815debe4fe50655b52ee))
* **deps:** update actions/checkout action to v4.2.0 ([ecac1eb](https://github.com/Collektive/collektive/commit/ecac1ebc48bae16ff0a100d22015a50d49510635))
* **deps:** update actions/checkout action to v4.2.1 ([0ee79d2](https://github.com/Collektive/collektive/commit/0ee79d21afb3ca770cae3747174bed09418ac409))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.10 ([cc527e9](https://github.com/Collektive/collektive/commit/cc527e93dc025711eca40892c3d2c705b7c09606))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.7 ([1e43cb0](https://github.com/Collektive/collektive/commit/1e43cb0216cc0a7e5cf05018036d10a95d3f5925))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.8 ([358f8f0](https://github.com/Collektive/collektive/commit/358f8f03e4e6ea47b42134e3bbdd4451293250ff))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.9 ([05578a0](https://github.com/Collektive/collektive/commit/05578a08d0764f7c09320527062ed5711ec516cf))
* enable website build and publish ([c120160](https://github.com/Collektive/collektive/commit/c12016022fa8e217265a8c0aa6fbc6b02d83e3f7))
* fix token name ([5b5fcad](https://github.com/Collektive/collektive/commit/5b5fcadb1283791d9229818ec37da726f98baff0))
* stop the Gradle Daemon on Windows before deployment to prevent "The process cannot access the file because it is being used by another process" ([a9a92e6](https://github.com/Collektive/collektive/commit/a9a92e6d2332502ecde13825af18db0771dc5d8b))

### Style improvements

* avoid star import ([cab883b](https://github.com/Collektive/collektive/commit/cab883bdb47ca34ae2aa1fed2ef6060e09c0d7e3))

### Refactoring

* **field:** change fold implementation to be not dependent from arrow ([52b7526](https://github.com/Collektive/collektive/commit/52b7526aa72d04305c4ab400f0c62ddb1b89509c))

## [10.3.2](https://github.com/Collektive/collektive/compare/10.3.1...10.3.2) (2024-09-20)

### Dependency updates

* **deps:** update dependency com.github.gmazzo.buildconfig to v5.5.0 ([3a36dae](https://github.com/Collektive/collektive/commit/3a36dae868687d0fd193adb0efd6cb27242cfa41))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.106 ([ca1ff69](https://github.com/Collektive/collektive/commit/ca1ff69e1414beae2917d5e65e56edb4fa7a207d))
* **deps:** update plugin publishoncentral to v5.1.7 ([5b7649d](https://github.com/Collektive/collektive/commit/5b7649d8ce7fbfcc86444a0cdf6e1aed4e29a157))

### Documentation

* add companion object kdoc ([53070fa](https://github.com/Collektive/collektive/commit/53070fa0f3920785c5cb230cf2beddcbe71e063c))

### Build and continuous integration

* **deps:** update actions/setup-node action to v4.0.4 ([53efd9f](https://github.com/Collektive/collektive/commit/53efd9ff3c1d2c96c0d4bae4484b7a4e8cffade8))

## [10.3.1](https://github.com/Collektive/collektive/compare/10.3.0...10.3.1) (2024-09-13)

### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.9.0 ([d06b1a4](https://github.com/Collektive/collektive/commit/d06b1a42f9525a82ce4cc1a3f1b741bf33918e11))
* **deps:** update alchemist to v34.1.4 ([485b86d](https://github.com/Collektive/collektive/commit/485b86d674566ae12a3cebdf579bb1e434b002e4))
* **deps:** update plugin multijvmtesting to v1.2.7 ([7343e54](https://github.com/Collektive/collektive/commit/7343e545e7ed99f8c4ff2f62c21cdfbe1aaa19c3))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.12 ([ab28284](https://github.com/Collektive/collektive/commit/ab2828449e1b0fd55c478b6201d92861ef786e2e))

### Build and continuous integration

* do not cancel in-progress builds ([b151f47](https://github.com/Collektive/collektive/commit/b151f47d9a95007b9da44744f14a39cb125d3b85))

## [10.3.0](https://github.com/Collektive/collektive/compare/10.2.0...10.3.0) (2024-09-13)

### Features

* **compiler-plugin:** add two simple checkers on the frontend part ([#454](https://github.com/Collektive/collektive/issues/454)) ([391b21e](https://github.com/Collektive/collektive/commit/391b21ec8927760fe2f9775c549eecd48b4c33a4))

### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.102 ([f9de327](https://github.com/Collektive/collektive/commit/f9de3273012938353e4ea5917898a38ecb78b6bf))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.103 ([14db92c](https://github.com/Collektive/collektive/commit/14db92c2ded079c3fd0bb08aece22cfc7b5b2c62))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.105 ([e1e3d0d](https://github.com/Collektive/collektive/commit/e1e3d0db317df33516486792d9573830345a1460))
* **deps:** update plugin com.gradle.develocity to v3.18.1 ([45ecbd7](https://github.com/Collektive/collektive/commit/45ecbd7b05c36761a994a61c43c54624fe6739de))
* **deps:** update plugin gradlepluginpublish to v1.3.0 ([da520f4](https://github.com/Collektive/collektive/commit/da520f4ebbdf78fc6be0b32ddc73e1006b44df70))
* **deps:** update plugin multijvmtesting to v1.2.6 ([bd98504](https://github.com/Collektive/collektive/commit/bd98504b186481fc5d0efc9cfc3119c65e5bd656))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.10 ([ef72b03](https://github.com/Collektive/collektive/commit/ef72b034f525e151aad2187203db93b390ad5ba5))

### Documentation

* document public companions ([2421855](https://github.com/Collektive/collektive/commit/242185564a59646c2252cc75e46092fddc6e94b5))

### Build and continuous integration

* change the concurrency group of staging-repo to prevent interleaving across branches ([b9c99e8](https://github.com/Collektive/collektive/commit/b9c99e8c484aac9c558daf933ddd72449aa82b0f))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.5 ([12aa27b](https://github.com/Collektive/collektive/commit/12aa27b95c17fc0ffd0c221edb01182d9540be7a))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.6 ([9fcb40e](https://github.com/Collektive/collektive/commit/9fcb40ed60b4f45f40bef8b122540a9e9389915a))
* **renovate:** disable automerge ([001ebbc](https://github.com/Collektive/collektive/commit/001ebbcd5d9a2032f86b3c707c03a3d0e3ff919e))

### Refactoring

* **alchemist-incarnation-collektive:** restrict the visibility of companion with private members only ([e4c1947](https://github.com/Collektive/collektive/commit/e4c19479d39cef0c19fc2d68275f6c807f5a6f45))

## [10.2.0](https://github.com/Collektive/collektive/compare/10.1.3...10.2.0) (2024-09-05)

### Features

* introduce the gradientCast ([G operator](https://doi.org/10.1145/3177774)) ([cfa6d81](https://github.com/Collektive/collektive/commit/cfa6d8109c920ca3723f176ba5507ebcf46b1b7c))
* **stdlib:** add `distanceTo` ([e797eb1](https://github.com/Collektive/collektive/commit/e797eb1c452001a6eb44ad34ce84800e011f6289))
* **test-tooling:** create the test-tooling subproject ([21ae4ac](https://github.com/Collektive/collektive/commit/21ae4acbeac6a892f5f4a1962cb2e8fe29715675))

### Dependency updates

* **deps:** update alchemist to v34.1.0 ([a194b2c](https://github.com/Collektive/collektive/commit/a194b2cde6bb4f9672980cb423f1e06c907e069a))
* **deps:** update alchemist to v34.1.1 ([7426995](https://github.com/Collektive/collektive/commit/7426995661e42b10ebf9531e3a8b646f31149d8f))
* **deps:** update alchemist to v34.1.2 ([5fde81b](https://github.com/Collektive/collektive/commit/5fde81b131651384ead9e55ccdf42659c189ab03))
* **deps:** update alchemist to v34.1.3 ([48d8275](https://github.com/Collektive/collektive/commit/48d8275ff385747e40350c52d95dc2655da153d7))
* **deps:** update dependency gradle to v8.10 ([02ae6d6](https://github.com/Collektive/collektive/commit/02ae6d66998fe48ed243627ec1e40617694aa575))
* **deps:** update dependency org.slf4j:slf4j-api to v2.0.14 ([9a5c95b](https://github.com/Collektive/collektive/commit/9a5c95b7bfd8e4a74e4adcdb85e208bdc51c3d27))
* **deps:** update dependency org.slf4j:slf4j-api to v2.0.15 ([72d2922](https://github.com/Collektive/collektive/commit/72d2922c09837c73076aa375a4b2d2faf91af0af))
* **deps:** update dependency org.slf4j:slf4j-api to v2.0.16 ([1b1a796](https://github.com/Collektive/collektive/commit/1b1a796547a5fb85a85ffba095484a94749be018))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.100 ([33e866e](https://github.com/Collektive/collektive/commit/33e866e019b5f2ce7ba0570497c0cbffa6f47835))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.101 ([dc48b96](https://github.com/Collektive/collektive/commit/dc48b96e3f7449cc430839d60641b556c5e154a1))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.96 ([edc1337](https://github.com/Collektive/collektive/commit/edc133760ab4502a286a4e0ff94c30dd2bec3cbb))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.97 ([21b96fb](https://github.com/Collektive/collektive/commit/21b96fb3a6fe9f1b82bacbe340dcb0c0db167acb))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.98 ([9ec46a6](https://github.com/Collektive/collektive/commit/9ec46a63e04d9aa20ae78192c8a3820a506d2cae))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.99 ([cad19f8](https://github.com/Collektive/collektive/commit/cad19f8ac41e646f157d2efc3640de1f5d97da3b))
* **deps:** update node.js to 20.17 ([7424283](https://github.com/Collektive/collektive/commit/7424283553b3ffcd7f00003cd18cf27c836b4078))
* **deps:** update plugin com.gradle.develocity to v3.18 ([6acaeca](https://github.com/Collektive/collektive/commit/6acaeca2898496b818b7e9a873aebf726fd22d67))
* **deps:** update plugin gradlepluginpublish to v1.2.2 ([b9999ca](https://github.com/Collektive/collektive/commit/b9999caad79fa6b0bedece406ddf4d2f2c41d021))
* **deps:** update plugin kotlin-qa to v0.65.1 ([f3fc463](https://github.com/Collektive/collektive/commit/f3fc46378bfd0e3a8511def90f7ad417f269b393))
* **deps:** update plugin kotlin-qa to v0.65.2 ([c811253](https://github.com/Collektive/collektive/commit/c81125327baff436c26fd69a033aedf309a05a25))
* **deps:** update plugin kotlin-qa to v0.66.0 ([c74023d](https://github.com/Collektive/collektive/commit/c74023d95f523611828c96bf59b3c043dbbc2faf))
* **deps:** update plugin kotlin-qa to v0.66.1 ([828b1b6](https://github.com/Collektive/collektive/commit/828b1b65493f2f191d8577bf379c3ae0abb8f533))
* **deps:** update plugin multijvmtesting to v1.2.4 ([67d8c51](https://github.com/Collektive/collektive/commit/67d8c510b30e2d82c2fbfe1ddb93ddc52bc595f8))
* **deps:** update plugin multijvmtesting to v1.2.5 ([915e629](https://github.com/Collektive/collektive/commit/915e629158b67b1ce662c51faa01d9e39214b42e))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.9 ([fe92b05](https://github.com/Collektive/collektive/commit/fe92b05813b97216e8de5a417226c7fadd5c4340))
* **deps:** update plugin publishoncentral to v5.1.5 ([e2490ca](https://github.com/Collektive/collektive/commit/e2490ca48c65187d538b5ded6a98acf121ad0725))
* **deps:** update plugin publishoncentral to v5.1.6 ([4899cad](https://github.com/Collektive/collektive/commit/4899cad6efd02b710b2cb65efddfcd2ba4181d0f))

### Bug Fixes

* **test-tooling:** make the node program private ([eca4d00](https://github.com/Collektive/collektive/commit/eca4d00be340292a884dc6b12abe1cd134cceeef))

### Tests

* **stdlib:** clarify as per [@nicolasfara](https://github.com/nicolasfara)'s suggestion ([2e2c6f1](https://github.com/Collektive/collektive/commit/2e2c6f16259207ec285e21773f5efe04e45f5c6d))
* **stdlib:** write a distanceTo test ([de77e97](https://github.com/Collektive/collektive/commit/de77e97639a06d629ddef00c708fcdc5a1237e18))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.3 ([1e79a59](https://github.com/Collektive/collektive/commit/1e79a590ec1fab78ec3ef5113c31e01cc6e3b14d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.4 ([b52f6fc](https://github.com/Collektive/collektive/commit/b52f6fcddd88a7044d03fddfe682bc2d71f2e17a))
* **dsl:** write projects first in the dependency list ([fb94f58](https://github.com/Collektive/collektive/commit/fb94f58304f2902a98827311c4cefd7593b92d99))
* **stdlib:** import the kotest junit5 jvm runner for testing ([3a3f32f](https://github.com/Collektive/collektive/commit/3a3f32fe4b934fbed3c1025a0d95c66ac90f1b43))

### General maintenance

* setup the copyright header in Idea ([6e50d41](https://github.com/Collektive/collektive/commit/6e50d419d7c924e530f1c1c32854e1c54b34bf50))

### Style improvements

* **stdlib:** apply [@cric96](https://github.com/cric96)'s suggestions ([0f278f5](https://github.com/Collektive/collektive/commit/0f278f5722f730ee50814b040c13c3309ad8981b))

## [10.1.3](https://github.com/Collektive/collektive/compare/10.1.2...10.1.3) (2024-08-06)

### Dependency updates

* **core-deps:** update kotlin monorepo to v2.0.10 ([841aed9](https://github.com/Collektive/collektive/commit/841aed965be53210670a2735bc5c288c274f46f2))
* **deps:** update alchemist to v34.0.14 ([140f667](https://github.com/Collektive/collektive/commit/140f667c96d321d93baf1ba92c7b9ae9be75cd7e))
* **deps:** update alchemist to v34.0.15 ([482539e](https://github.com/Collektive/collektive/commit/482539e7e221afd7697b019d7bf23926403d1b84))
* **deps:** update alchemist to v34.0.16 ([175458f](https://github.com/Collektive/collektive/commit/175458f8962536e68410b3a0acfea47800c03db3))
* **deps:** update alchemist to v34.0.17 ([8b32537](https://github.com/Collektive/collektive/commit/8b32537e7b95a92967ae31fe98548119d6657a30))
* **deps:** update alchemist to v34.0.18 ([3e66a86](https://github.com/Collektive/collektive/commit/3e66a86a438c6af74a1c2a7917bf66de26c28624))
* **deps:** update alchemist to v34.0.19 ([46acbf2](https://github.com/Collektive/collektive/commit/46acbf2ccc8b2d495411d0884ef9a6d42d4b8f34))
* **deps:** update alchemist to v34.0.20 ([4bb268d](https://github.com/Collektive/collektive/commit/4bb268d0ca86f282355a60cbedee5dd2e870dd36))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.92 ([204fa96](https://github.com/Collektive/collektive/commit/204fa963f3262a917d17bf7dfa72d9dcccb6af02))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.93 ([4e540cb](https://github.com/Collektive/collektive/commit/4e540cb284132526c7892d714b078ce72acb0042))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.94 ([3b7ac4b](https://github.com/Collektive/collektive/commit/3b7ac4b9d2551132f5b45f610cfc5fb665001d91))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.95 ([3bba9e2](https://github.com/Collektive/collektive/commit/3bba9e2eb46aef4dc4cf6420e839cb73d304b859))
* **deps:** update node.js to 20.16 ([dcc3066](https://github.com/Collektive/collektive/commit/dcc30664a4382d25d2359aaa9594b48115d4338c))
* **deps:** update plugin com.gradle.develocity to v3.17.6 ([ebfeb0b](https://github.com/Collektive/collektive/commit/ebfeb0b9feaa7da8ed8c62d8e60cffd79a745fec))
* **deps:** update plugin kotlin-qa to v0.65.0 ([#408](https://github.com/Collektive/collektive/issues/408)) ([86e352f](https://github.com/Collektive/collektive/commit/86e352f13b408029888dfb55b5e0c75c296a56de))
* **deps:** update plugin multijvmtesting to v1.2.0 ([daa8b04](https://github.com/Collektive/collektive/commit/daa8b042656e77846236ac1ea3be5460d82cbea9))
* **deps:** update plugin multijvmtesting to v1.2.2 ([2b05672](https://github.com/Collektive/collektive/commit/2b0567209a009fd6fecdb87ae40411e8bb6c5c07))
* **deps:** update plugin multijvmtesting to v1.2.3 ([0a5eb03](https://github.com/Collektive/collektive/commit/0a5eb035245c23c12b8def7b58ceb1553ae6e0ea))

### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.1 ([a010239](https://github.com/Collektive/collektive/commit/a010239b1864068fbfb19351af6f1c1c87591824))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.2 ([3129327](https://github.com/Collektive/collektive/commit/312932712bca41c0367741da5f869e07d470ffe5))

## [10.1.2](https://github.com/Collektive/collektive/compare/10.1.1...10.1.2) (2024-07-19)

### Dependency updates

* **core-deps:** update plugin kover to v0.8.3 ([800f33c](https://github.com/Collektive/collektive/commit/800f33ca05f641e35725bcdb1470f74431671c58))
* **deps:** update dependency com.squareup:kotlinpoet to v1.18.1 ([196b0c9](https://github.com/Collektive/collektive/commit/196b0c9a26d1666f5a80404d72907a7e6db5c25a))
* **deps:** update dependency commons-codec:commons-codec to v1.17.1 ([e189bbe](https://github.com/Collektive/collektive/commit/e189bbee0ab483b0d8831f542e8ad0fce43a54f1))
* **deps:** update dependency gradle to v8.9 ([8d54347](https://github.com/Collektive/collektive/commit/8d54347287ab2001b778719c467c72c00bfb09ee))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.90 ([0fdc35b](https://github.com/Collektive/collektive/commit/0fdc35bf19601c27cd00de8b597378237b729354))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.91 ([beb4aa4](https://github.com/Collektive/collektive/commit/beb4aa4baf4681188276a6501616d6e27d553199))
* **deps:** update plugin kotlin-qa to v0.62.4 ([998c6dd](https://github.com/Collektive/collektive/commit/998c6dde07fae4fbb8f49e0dd95c4e063e0363ef))
* **deps:** update plugin multijvmtesting to v1 ([ff462e3](https://github.com/Collektive/collektive/commit/ff462e36a8dc76e73c2e744e983931a1625216a0))
* **deps:** update plugin multijvmtesting to v1.0.1 ([f66751e](https://github.com/Collektive/collektive/commit/f66751e4f168e807777e433871e592b64c46798f))
* **deps:** update plugin multijvmtesting to v1.0.3 ([47826e8](https://github.com/Collektive/collektive/commit/47826e89ccc653c46e630a29c89b7df9b12fbb6a))
* **deps:** update plugin multijvmtesting to v1.0.4 ([a82332e](https://github.com/Collektive/collektive/commit/a82332ea4199a84e431f22767311f5adba9f00de))
* **deps:** update plugin multijvmtesting to v1.1.0 ([760184c](https://github.com/Collektive/collektive/commit/760184cfb5a328c88a85132d535402c94a5c2704))
* **deps:** update plugin multijvmtesting to v1.1.1 ([6308b89](https://github.com/Collektive/collektive/commit/6308b893acc19f72b4910db8beeb0e95f4cd8b6a))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.8 ([cafd016](https://github.com/Collektive/collektive/commit/cafd016fcec8a3c859d8dd4b6e363ee01f018a88))
* **deps:** update plugin publishoncentral to v5.1.4 ([16343b5](https://github.com/Collektive/collektive/commit/16343b558a172112cb2e823c03424df2c94d0024))

### Build and continuous integration

* add explicit codecov token ([4aba5e2](https://github.com/Collektive/collektive/commit/4aba5e2d24ba155da7ac2af5a510422548e5d5bb))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.1.0 ([5e734ff](https://github.com/Collektive/collektive/commit/5e734fff5fcad2b86ed81db7f835fd89e96f7e24))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.1.1 ([87b78e9](https://github.com/Collektive/collektive/commit/87b78e9e1dd69dbda58ad6726e1fdebb46f58d29))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.3.0 ([07f8073](https://github.com/Collektive/collektive/commit/07f8073edd6a4839d5bbbe74270de976f72b9ecd))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.4.0 ([556d6b2](https://github.com/Collektive/collektive/commit/556d6b2a53a515c027359b95188d5cfc2055cbfc))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3.5.0 ([4f35673](https://github.com/Collektive/collektive/commit/4f35673ae1122fad5dc3e4f45c95c74d6567d38a))

## [10.1.1](https://github.com/Collektive/collektive/compare/10.1.0...10.1.1) (2024-07-09)

### Dependency updates

* **core-deps:** update plugin kover to v0.8.2 ([7131e63](https://github.com/Collektive/collektive/commit/7131e63c9c279c2a3cf552339bce16e7503a67a0))

### Build and continuous integration

* **deps:** update actions/setup-node action to v4.0.3 ([e285c05](https://github.com/Collektive/collektive/commit/e285c050260d1434bae176ce1059815d63b14020))
* use new kover api ([ea335f7](https://github.com/Collektive/collektive/commit/ea335f7571cda9e86370d89fcee9bb5a60e9b283))

## [10.1.0](https://github.com/Collektive/collektive/compare/10.0.0...10.1.0) (2024-07-09)

### Features

* add `sum`, `count`, `all`, `any` and `none` field operations ([#346](https://github.com/Collektive/collektive/issues/346)) ([14c356f](https://github.com/Collektive/collektive/commit/14c356f3bce2e835936e35cdc795ed8d3a271a40))

### Dependency updates

* **deps:** update dependency com.github.gmazzo.buildconfig to v5.4.0 ([4e2c2ba](https://github.com/Collektive/collektive/commit/4e2c2ba6f58cbb42829397fb9edf8ff6943c8501))
* **deps:** update dependency com.github.tschuchortdev:kotlin-compile-testing to v1.6.0 ([7f3c620](https://github.com/Collektive/collektive/commit/7f3c620a82b80d492de2f544f9ceca690055250a))
* **deps:** update dependency com.squareup:kotlinpoet to v1.18.0 ([71558e6](https://github.com/Collektive/collektive/commit/71558e69e42a53ac1495ebb7e1b47aa6d01caf16))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.86 ([bf2ba04](https://github.com/Collektive/collektive/commit/bf2ba04380cb33e9a4df2d5c3e4a9b3631f171a6))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.87 ([672a001](https://github.com/Collektive/collektive/commit/672a00144f283ac7f53a738f9ea8824c291d27dc))

### Build and continuous integration

* remove deprecated kotlin compilation task ([8cbef74](https://github.com/Collektive/collektive/commit/8cbef748f71d6680f06322e06518386cffa96af4))
* update action to a fixed version ([b52caa6](https://github.com/Collektive/collektive/commit/b52caa69ec8406d1fc612e22399f3f6b62964086))

### General maintenance

* transform to mjs ([43a650f](https://github.com/Collektive/collektive/commit/43a650f4c49c32a898b38c8f318ea142cb361251))
* update semantic release preconfigured version ([a1bb4d3](https://github.com/Collektive/collektive/commit/a1bb4d38e09f335ff95e3834b274a42ee24b3fc6))

## [10.0.0](https://github.com/Collektive/collektive/compare/9.2.4...10.0.0) (2024-07-02)


### ⚠ BREAKING CHANGES

* generate library code for field operations on kotlin standard library types (#273)

### Features

* generate library code for field operations on kotlin standard library types ([#273](https://github.com/Collektive/collektive/issues/273)) ([31545f2](https://github.com/Collektive/collektive/commit/31545f2e887bd1d9cb618fa973b27d99b4163b6a)), closes [square/kotlinpoet#1933](https://github.com/square/kotlinpoet/issues/1933) [square/kotlinpoet#1933](https://github.com/square/kotlinpoet/issues/1933)


### Dependency updates

* **deps:** update alchemist to v34.0.10 ([b666dc1](https://github.com/Collektive/collektive/commit/b666dc155c128d89d487ba3c90f4f7124955071a))
* **deps:** update alchemist to v34.0.11 ([174eace](https://github.com/Collektive/collektive/commit/174eacedc08998eed111bd5a12403e9b2d6cc53c))
* **deps:** update alchemist to v34.0.13 ([6890653](https://github.com/Collektive/collektive/commit/6890653812e76fb4b44bffba102fb720d657c828))
* **deps:** update alchemist to v34.0.8 ([ad9b277](https://github.com/Collektive/collektive/commit/ad9b2777d8daa3d42a017775dcd923c01f2c82db))
* **deps:** update alchemist to v34.0.9 ([871197f](https://github.com/Collektive/collektive/commit/871197ffc35c89820b1b2af6e46e351eed3b7da3))
* **deps:** update dependency com.squareup:kotlinpoet to v1.17.0 ([8b27c9f](https://github.com/Collektive/collektive/commit/8b27c9fea6c0da5b021fe11de359229188ba83e7))
* **deps:** update dependency gradle to v8.8 ([d29b256](https://github.com/Collektive/collektive/commit/d29b2568d4c9fc539a341b829e41215ca1d84553))
* **deps:** update dependency io.kotest.multiplatform to v5.9.1 ([930081c](https://github.com/Collektive/collektive/commit/930081c474a447ea8e44b1a3cba4848898dce67d))
* **deps:** update dependency it.unibo.alchemist:alchemist-api to v34.0.7 ([f183563](https://github.com/Collektive/collektive/commit/f183563f50d1a48cbde27e28cd4c522c014e0d9c))
* **deps:** update node.js to 20.14 ([ecba005](https://github.com/Collektive/collektive/commit/ecba00566e6dbb7583f486e43bfdb7b3aff5852b))
* **deps:** update node.js to 20.15 ([dc8d650](https://github.com/Collektive/collektive/commit/dc8d650d329e07c680079f38fe53dd7c337dd1c9))
* **deps:** update plugin com.gradle.develocity to v3.17.5 ([ffe79c7](https://github.com/Collektive/collektive/commit/ffe79c7aec57e68bf4184aa5a3c9a2d55bab6abc))
* **deps:** update plugin gitsemver to v3.1.7 ([bad3765](https://github.com/Collektive/collektive/commit/bad3765fa172e353371b615119e69017c5c4e346))
* **deps:** update plugin kotlin-qa to v0.62.1 ([f43dc25](https://github.com/Collektive/collektive/commit/f43dc25a35974adbe04d880475abeca50d9f2610))
* **deps:** update plugin kotlin-qa to v0.62.2 ([870a894](https://github.com/Collektive/collektive/commit/870a8943153d2cec671b2c5c59dc44405b7336e4))
* **deps:** update plugin kotlin-qa to v0.62.3 ([1eceb1f](https://github.com/Collektive/collektive/commit/1eceb1f2b563f717386521d39d4c89faf20e128b))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.7 ([adb4c57](https://github.com/Collektive/collektive/commit/adb4c57f214aed942c76f87608984d285b1bb1c2))
* **deps:** update plugin publishoncentral to v5.1.2 ([cccde5b](https://github.com/Collektive/collektive/commit/cccde5b48017fc54afc883e026f55c0f05fafe9c))
* **deps:** update plugin publishoncentral to v5.1.3 ([9f3a73a](https://github.com/Collektive/collektive/commit/9f3a73a337830ca1632e493248993ded3cc7dc00))
* **deps:** update plugin tasktree to v4 ([1ac746d](https://github.com/Collektive/collektive/commit/1ac746d0bd6e1ebf02ba2ac82186dafc06793fff))


### Build and continuous integration

* **deps:** update actions/checkout action to v4.1.7 ([7068f95](https://github.com/Collektive/collektive/commit/7068f9517b9249c712710cf39707e80b36ed88fd))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.21 ([4ad0f89](https://github.com/Collektive/collektive/commit/4ad0f89411d7ed981b0a9f59d449aca8a59bbce4))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.22 ([a3a46f3](https://github.com/Collektive/collektive/commit/a3a46f34ff64995a838ef1ad7da819c199a6784c))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.23 ([9aa3c31](https://github.com/Collektive/collektive/commit/9aa3c31da77551f0ae9ae5f2dac93fcb3c1cddd3))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.24 ([8489a81](https://github.com/Collektive/collektive/commit/8489a818c39c54d63cd4420a2f22898f4a0ba734))
* **deps:** update danysk/build-check-deploy-gradle-action action to v3 ([a3fd0a7](https://github.com/Collektive/collektive/commit/a3fd0a74384714337dfed2557a6cfde4be989c85))

## [9.2.4](https://github.com/Collektive/collektive/compare/9.2.3...9.2.4) (2024-05-23)


### Dependency updates

* **core-deps:** update kotlin monorepo to v2 ([22d1dfc](https://github.com/Collektive/collektive/commit/22d1dfcf3b957963763db6195b20c77bb5e49218))
* **deps:** update alchemist to v34.0.4 ([3960061](https://github.com/Collektive/collektive/commit/3960061695c78d026f49978fc53947d298d2bae0))
* **deps:** update alchemist to v34.0.5 ([7e90764](https://github.com/Collektive/collektive/commit/7e9076481e68ce5d3fc9ab3dc3cd9eec0c8b82f5))
* **deps:** update alchemist to v34.0.6 ([eee9298](https://github.com/Collektive/collektive/commit/eee9298b3c0130fae1caaae26891cb7ea758f109))
* **deps:** update plugin com.gradle.develocity to v3.17.4 ([11eb4d5](https://github.com/Collektive/collektive/commit/11eb4d5533d36137b93fe349be7eda904a05296d))
* **deps:** update plugin gitsemver to v3.1.6 ([26a963d](https://github.com/Collektive/collektive/commit/26a963da72181527b3c030ad352afe7c9ba955c5))


### Build and continuous integration

* add new kotlin 2.0 requirements ([bfd7032](https://github.com/Collektive/collektive/commit/bfd7032c796a38b6515387fab76241250f21354c))
* **deps:** update actions/checkout action to v4.1.6 ([6dacf1d](https://github.com/Collektive/collektive/commit/6dacf1d7c6b6ec5c30fb1acf68754ffe9e617c70))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.19 ([a3f772e](https://github.com/Collektive/collektive/commit/a3f772e27362e14627d68155f7511ab6686b4cbc))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.20 ([f38c4ce](https://github.com/Collektive/collektive/commit/f38c4ceb70576412e417c12fd8b54bb015be31ee))
* **deps:** update dependency macos github actions runner to v14 ([ae0e3f9](https://github.com/Collektive/collektive/commit/ae0e3f9619a68dfe0002ed4d2162b96e6b124adb))
* **deps:** update dependency ubuntu github actions runner to v24 ([85e18d3](https://github.com/Collektive/collektive/commit/85e18d3b21b0d946e8e08dc2082473d4829bce25))
* migrate to develocity plugin ([bbaf167](https://github.com/Collektive/collektive/commit/bbaf167fd95d66bf7066c6ba6ee6e7a1d3f958ce))


### General maintenance

* add opt-in ([4bd75e9](https://github.com/Collektive/collektive/commit/4bd75e95a89025e6a43e6b5315555808b19f3a23))


### Refactoring

* change alignment implementation via compiler plugin to be debug-friendly (issue [#337](https://github.com/Collektive/collektive/issues/337)) ([#347](https://github.com/Collektive/collektive/issues/347)) ([c4ac438](https://github.com/Collektive/collektive/commit/c4ac438ed4554a64eb677430c26d8b4aa70f30be))

## [9.2.3](https://github.com/Collektive/collektive/compare/9.2.2...9.2.3) (2024-05-10)


### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.8.1 ([7cb770a](https://github.com/Collektive/collektive/commit/7cb770ae25611c982bf7829adcd6e99316a7bf8c))
* **deps:** update alchemist to v34.0.2 ([a61a92d](https://github.com/Collektive/collektive/commit/a61a92dee031138e198b555205cb61459921c662))
* **deps:** update dependency it.unibo.alchemist:alchemist-api to v34.0.3 ([76022f0](https://github.com/Collektive/collektive/commit/76022f04e1d5be76dacb78fd211bccea3e7c5cfd))
* **deps:** update kotest to v5.9.0 ([a6a4816](https://github.com/Collektive/collektive/commit/a6a4816cba7e8718d1f842798461fae32f7d2488))
* **deps:** update node.js to 20.13 ([f0d02a3](https://github.com/Collektive/collektive/commit/f0d02a35aa611bf9e73bbbfec579c57f189a99c8))
* **deps:** update plugin com.gradle.enterprise to v3.17.3 ([b1922a5](https://github.com/Collektive/collektive/commit/b1922a52b0e29dfde76f3ae8554686764ccb5171))
* **deps:** update plugin gitsemver to v3.1.5 ([f1cdef6](https://github.com/Collektive/collektive/commit/f1cdef6f471282032ce4322fe7a62befa8b3dd90))
* **deps:** update plugin kotlin-qa to v0.61.1 ([aeb08c5](https://github.com/Collektive/collektive/commit/aeb08c5b9f9ec66741996f6cb2fa9b25949b0aa9))
* **deps:** update plugin kotlin-qa to v0.62.0 ([29c4d9e](https://github.com/Collektive/collektive/commit/29c4d9e8ac123612bf95da072719ba32855a020e))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.5 ([8ddfbd5](https://github.com/Collektive/collektive/commit/8ddfbd5621ac47a494f8bac20f8903fc8ec91905))
* **deps:** update plugin publishoncentral to v5.1.1 ([2165d14](https://github.com/Collektive/collektive/commit/2165d14888d50b6b49446a9a11dec82cd9332522))


### Build and continuous integration

* **deps:** update actions/checkout action to v4.1.5 ([1f02dcf](https://github.com/Collektive/collektive/commit/1f02dcf0f749f83dce17efab2c494b8fdbbbc196))

## [9.2.2](https://github.com/Collektive/collektive/compare/9.2.1...9.2.2) (2024-05-07)


### Dependency updates

* **core-deps:** update kotlin monorepo to v1.9.24 ([05f8ad3](https://github.com/Collektive/collektive/commit/05f8ad39b90f9df237e41473c8c47c588c9b3e84))
* **deps:** update alchemist to v33.1.0 ([674e227](https://github.com/Collektive/collektive/commit/674e2272fc21e420c4384d50dc5637bac3f609df))
* **deps:** update alchemist to v33.1.1 ([d1b3778](https://github.com/Collektive/collektive/commit/d1b37788800638bb1cd5e02a5d4e519a5fa6581d))
* **deps:** update alchemist to v33.1.2 ([54ea599](https://github.com/Collektive/collektive/commit/54ea59948da855e18f1a0b483622440916a5fe44))
* **deps:** update alchemist to v34 ([53530f9](https://github.com/Collektive/collektive/commit/53530f9eb3af24c52f96c8d942accf615ddca8be))
* **deps:** update dependency commons-codec:commons-codec to v1.17.0 ([8ef8539](https://github.com/Collektive/collektive/commit/8ef8539d60fe9b522dcaeec296612c549f30d0aa))


### Build and continuous integration

* **deps:** update actions/checkout action to v4.1.3 ([9328b39](https://github.com/Collektive/collektive/commit/9328b39cb94331cc479caa5340c71be1cc755e00))
* **deps:** update actions/checkout action to v4.1.4 ([8d83710](https://github.com/Collektive/collektive/commit/8d83710cab5f6406be858e4902710bb086e931d6))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.17 ([3de5f7a](https://github.com/Collektive/collektive/commit/3de5f7a07d39e59ce8083f06c7db977a565f0e1d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.18 ([65af96a](https://github.com/Collektive/collektive/commit/65af96aaf0de5aa7132330da515c002ca1927f5c))

## [9.2.1](https://github.com/Collektive/collektive/compare/9.2.0...9.2.1) (2024-04-22)


### Bug Fixes

* remove parameter in clone action that caused the wrong assignation of ids to new nodes ([#318](https://github.com/Collektive/collektive/issues/318)) ([99c64eb](https://github.com/Collektive/collektive/commit/99c64eb18bc95359e67b77bfc580c09cbe0fb498))

## [9.2.0](https://github.com/Collektive/collektive/compare/9.1.3...9.2.0) (2024-04-22)


### Features

* **incarnation:** support any kind of classes extending `NodeProperty` required by the context receiver entrypoint ([#317](https://github.com/Collektive/collektive/issues/317)) ([9e8b938](https://github.com/Collektive/collektive/commit/9e8b938582dc43f6519aeaadd3c41761c64bece1))


### Dependency updates

* **deps:** update alchemist to v33.0.8 ([7edbdfe](https://github.com/Collektive/collektive/commit/7edbdfeb98e93c10c6788079feb899648d8c7bf6))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.16 ([57f9852](https://github.com/Collektive/collektive/commit/57f98526e9f1783efd4fb01c577dd1a72bbfef1a))

## [9.1.3](https://github.com/Collektive/collektive/compare/9.1.2...9.1.3) (2024-04-17)


### Bug Fixes

* change the return value via compiler plugin ([4d05e50](https://github.com/Collektive/collektive/commit/4d05e50ec1a3be6438ccc82413a6327558ff6850))
* define a function to check if the compiler plugin is applied ([8fc65f6](https://github.com/Collektive/collektive/commit/8fc65f663b798cf65a2d4162de0b1b698abed5b4))
* fail fast if the collektive compiler plugin is not applied ([76b7826](https://github.com/Collektive/collektive/commit/76b78263eb7bb44ed06f069dc27b78ef916eca11))


### Style improvements

* address [@danysk](https://github.com/danysk) comments ([ee056c0](https://github.com/Collektive/collektive/commit/ee056c0c7ad232f1bbe960328d06162373ed2596))
* address style issue ([71aa949](https://github.com/Collektive/collektive/commit/71aa94958d87d24b7ca05b0ab2e2e098875989be))

## [9.1.2](https://github.com/Collektive/collektive/compare/9.1.1...9.1.2) (2024-04-17)


### Dependency updates

* **deps:** update alchemist to v33.0.2 ([7339635](https://github.com/Collektive/collektive/commit/7339635262efb5841b3ffde98ec985f18160ac06))
* **deps:** update alchemist to v33.0.3 ([9734b93](https://github.com/Collektive/collektive/commit/9734b93e28067e60c45b41e0fad804c3127f76c3))
* **deps:** update alchemist to v33.0.4 ([d047410](https://github.com/Collektive/collektive/commit/d04741077049c4a779cc19d03e9d412cea69349e))
* **deps:** update alchemist to v33.0.5 ([d376d98](https://github.com/Collektive/collektive/commit/d376d9811c697ac06b3ab1796e215cf6f71f1a31))
* **deps:** update alchemist to v33.0.6 ([2d0d701](https://github.com/Collektive/collektive/commit/2d0d7014a4cc06394d960e12f8dba08f4550f83a))
* **deps:** update alchemist to v33.0.7 ([9454db6](https://github.com/Collektive/collektive/commit/9454db665bcca67ca37a20eafd4aaa7065e8b9c6))
* **deps:** update dependency io.arrow-kt:arrow-core to v1.2.4 ([4774227](https://github.com/Collektive/collektive/commit/4774227dec01e6174a44e01cf2c674f2cd000f4d))
* **deps:** update dependency org.slf4j:slf4j-api to v2.0.13 ([83f4f97](https://github.com/Collektive/collektive/commit/83f4f971b9f54142ac8c93401e7f94faed928f64))
* **deps:** update node.js to 20.12 ([7baa004](https://github.com/Collektive/collektive/commit/7baa0047e612be43c7d75d678f9b47d5d55f87cb))
* **deps:** update plugin com.gradle.enterprise to v3.17 ([454b3a3](https://github.com/Collektive/collektive/commit/454b3a3d68cfd3028c6185daedf36df87ccab004))
* **deps:** update plugin com.gradle.enterprise to v3.17.1 ([3a38dad](https://github.com/Collektive/collektive/commit/3a38dad2f7c58437474c38f91b82932a27678fcd))
* **deps:** update plugin com.gradle.enterprise to v3.17.2 ([80a005c](https://github.com/Collektive/collektive/commit/80a005ce8c4246c4682bb434117bc405648898e3))
* **deps:** update plugin publishoncentral to v5.1.0 ([817df0b](https://github.com/Collektive/collektive/commit/817df0b527a2d374db69ca9d9df1e4a23fdb6de1))
* **deps:** update plugin tasktree to v3 ([aaafdd9](https://github.com/Collektive/collektive/commit/aaafdd900840a2566563a5690f5b4fcf68b2269d))


### Bug Fixes

* update error message ([05e2f7d](https://github.com/Collektive/collektive/commit/05e2f7d7b89dcf5ae6fe3fc48b5549af3cc89c60))


### Tests

* update tests with new error message ([7aa2235](https://github.com/Collektive/collektive/commit/7aa2235c4ed869082ffc1429e4b9ad8c2680514d))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.12 ([548479f](https://github.com/Collektive/collektive/commit/548479fd0be19e423bbad55b8df5625af0a9bfa5))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.13 ([6058545](https://github.com/Collektive/collektive/commit/6058545543414e135c8b931f07056036146984ce))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.14 ([effb01f](https://github.com/Collektive/collektive/commit/effb01fec7ab745829cb5df9e7d93f8e6fb1fc15))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.15 ([e3a64da](https://github.com/Collektive/collektive/commit/e3a64da728168da6933d0745eba98af97af241df))

## [9.1.1](https://github.com/Collektive/collektive/compare/9.1.0...9.1.1) (2024-03-27)


### Dependency updates

* **deps:** update alchemist to v32.0.1 ([663dafc](https://github.com/Collektive/collektive/commit/663dafc84541b28ac9d5351838b5ad1ed73f4c0e))
* **deps:** update alchemist to v33 ([c0dda84](https://github.com/Collektive/collektive/commit/c0dda84eb8d239b23f02f41ce77901026a0dfc91))
* **deps:** update alchemist to v33.0.1 ([61ebc12](https://github.com/Collektive/collektive/commit/61ebc126c9ceb31da8d315945f7e89271563723c))
* **deps:** update dependency gradle to v8.7 ([b8c73c1](https://github.com/Collektive/collektive/commit/b8c73c1433f17b37fa2723267e5ca45602e4789e))
* **deps:** update plugin gitsemver to v3.1.4 ([f16b5f3](https://github.com/Collektive/collektive/commit/f16b5f3e85c0cf23d0bda9ac1aa7c32d562b95f6))
* **deps:** update plugin kotlin-qa to v0.60.4 ([d1fb8c4](https://github.com/Collektive/collektive/commit/d1fb8c4f6fff7ca49f5783f1fcd80f2a0940cdc0))
* **deps:** update plugin kotlin-qa to v0.61.0 ([affa897](https://github.com/Collektive/collektive/commit/affa897147f5869e69ccf26ee9d1e7f0d54b82c5))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.4 ([a21d6ad](https://github.com/Collektive/collektive/commit/a21d6adc68245760bddc92dbfe4bf203f58fbe9d))
* **deps:** update plugin publishoncentral to v5.0.26 ([6dfe951](https://github.com/Collektive/collektive/commit/6dfe951c1d70852eec662859cd5f5341c0bf947f))


### Performance improvements

* efficient implementation of `neighboring` ([#284](https://github.com/Collektive/collektive/issues/284)) ([8b95e4d](https://github.com/Collektive/collektive/commit/8b95e4dbe3145578de41ea9b67c1a927eecd96a6))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.10 ([5a718dc](https://github.com/Collektive/collektive/commit/5a718dc7b88feebe777eefe1034bda54b7c75210))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.11 ([04d7834](https://github.com/Collektive/collektive/commit/04d7834f0e489b6e0ab4196f5d45dc4ce2e51af7))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.9 ([f4e6e96](https://github.com/Collektive/collektive/commit/f4e6e9667d27a2c8c7e212e9c25c0b8230264b97))

## [9.1.0](https://github.com/Collektive/collektive/compare/9.0.3...9.1.0) (2024-03-18)


### Features

* update incarnation to adapt to new Alchemist version ([5fc35de](https://github.com/Collektive/collektive/commit/5fc35ded252a4094c16222c3859d8db3c886debb))


### Dependency updates

* **deps:** update Alchemist dependency ([9fc68bc](https://github.com/Collektive/collektive/commit/9fc68bcb1af3e7e714073b5fb6fb9d6b1bad833f))
* **deps:** update alchemist to v31.0.5 ([cb002a4](https://github.com/Collektive/collektive/commit/cb002a41fbaeb80c5532cae8f016a74e612f29f4))
* **deps:** update alchemist to v31.0.6 ([9b3f591](https://github.com/Collektive/collektive/commit/9b3f591f2f49bec27069612a4ddce081ae2deac6))
* **deps:** update kotest to v5.8.1 ([4a2ff02](https://github.com/Collektive/collektive/commit/4a2ff02ee2298146f52844565755359eb0db1f62))
* **deps:** update plugin gitsemver to v3.1.3 ([3918029](https://github.com/Collektive/collektive/commit/3918029cc3646102faf63f6b686176fd2b5f469e))


### Documentation

* update environment variables doc ([7235075](https://github.com/Collektive/collektive/commit/7235075de7dd3d74d0aa70240abeb900f81edc0b))


### Tests

* add test that uses envirnoment variables in the entrypoint of the program ([8f70616](https://github.com/Collektive/collektive/commit/8f7061689c481623db57f664ae5a2d5a2c1b267f))


### Build and continuous integration

* **deps:** update actions/checkout action to v4.1.2 ([bed3371](https://github.com/Collektive/collektive/commit/bed337147bd7156b536ae1ef652aeb6bc9b0d257))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.7 ([29ed3c6](https://github.com/Collektive/collektive/commit/29ed3c6151588091a90f44729eea9b40ea8b0fbd))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.8 ([b31c77d](https://github.com/Collektive/collektive/commit/b31c77d98c5b0c4ce6b3cfed729fa5ffbd142bcd))


### Refactoring

* rename parameter name ([bca4dcd](https://github.com/Collektive/collektive/commit/bca4dcdd6de1703fd04253099d23f085142737c0))

## [9.0.3](https://github.com/Collektive/collektive/compare/9.0.2...9.0.3) (2024-03-07)


### Dependency updates

* **core-deps:** update kotlin monorepo to v1.9.23 ([8393427](https://github.com/Collektive/collektive/commit/8393427fe1b3f3c50a490cedde13caad3b874226))
* **deps:** update plugin kotlin-qa to v0.60.3 ([8d63a83](https://github.com/Collektive/collektive/commit/8d63a83f25ab2897b3a51716e5d6e65bb23deabe))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.3 ([bcb2b88](https://github.com/Collektive/collektive/commit/bcb2b8888beb9223b6097174c3364ad0870bd4fb))
* **deps:** update plugin publishoncentral to v5.0.25 ([564e9de](https://github.com/Collektive/collektive/commit/564e9de7bf87d882320d278f09b66667c7fcb614))

## [9.0.2](https://github.com/Collektive/collektive/compare/9.0.1...9.0.2) (2024-03-06)


### Bug Fixes

* repeating cannot return Fields ([4b9bf76](https://github.com/Collektive/collektive/commit/4b9bf76c0e6dc96d659951b5e7e31a5ee89170a8))


### Tests

* add regression test checking that an exception is raised when the repeat returns a Field ([9a63615](https://github.com/Collektive/collektive/commit/9a6361550b302a1f4fd3cc5edaf03e13e6099b49))


### Style improvements

* remove uneeded comments ([6f84a7b](https://github.com/Collektive/collektive/commit/6f84a7bcd3efe849b0e6d06842106b43bee0dc2b))

## [9.0.1](https://github.com/Collektive/collektive/compare/9.0.0...9.0.1) (2024-03-06)


### Bug Fixes

* fix broken support for aligned mapping of nullable fields ([a35976a](https://github.com/Collektive/collektive/commit/a35976a7904908a36c04efd89cc418c6a8fe05d0))


### Build and continuous integration

* enforce check also in plugin projects ([c5c724c](https://github.com/Collektive/collektive/commit/c5c724c1b382dff607b69e86f2cbbc1bb445b820))


### Style improvements

* format code according to style rules ([c47e563](https://github.com/Collektive/collektive/commit/c47e563316593262daadf3ce5a5de56f60f61078))

## [9.0.0](https://github.com/Collektive/collektive/compare/8.0.0...9.0.0) (2024-03-05)


### ⚠ BREAKING CHANGES

* alignment performance optimization (#250)

### Dependency updates

* **deps:** update alchemist to v31.0.3 ([f3b4b3d](https://github.com/Collektive/collektive/commit/f3b4b3d1bcfb7113bc4857a6dbcd6fc081d35219))
* **deps:** update alchemist to v31.0.4 ([04620fa](https://github.com/Collektive/collektive/commit/04620fabc2c8e4170fb32ea2c44ef03d50e5dd42))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.2 ([c89cb51](https://github.com/Collektive/collektive/commit/c89cb51ae86640a65e0f1e7c85069b142eab4450))


### Performance improvements

* alignment performance optimization ([#250](https://github.com/Collektive/collektive/issues/250)) ([5a0d5e0](https://github.com/Collektive/collektive/commit/5a0d5e0d6be65a019e2029514c659c1e9eec36a1))

## [8.0.0](https://github.com/Collektive/collektive/compare/7.0.6...8.0.0) (2024-03-04)


### ⚠ BREAKING CHANGES

* **alchemist-incarnation:** add collektive-scripting support (#244)

### Features

* **alchemist-incarnation:** add collektive-scripting support ([#244](https://github.com/Collektive/collektive/issues/244)) ([985de20](https://github.com/Collektive/collektive/commit/985de2020a11f6f3931939c4ffbd3432592885c6))


### Dependency updates

* **deps:** update plugin gitsemver to v3.1.2 ([c06f20f](https://github.com/Collektive/collektive/commit/c06f20fb30072b3b66aa01c2fae693fe26bccfcf))
* **deps:** update plugin kotlin-qa to v0.60.2 ([caf3e8b](https://github.com/Collektive/collektive/commit/caf3e8bd4c5cce1e95f36a1aa2f17f56ee4b189f))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.2 ([871dfd4](https://github.com/Collektive/collektive/commit/871dfd48121d28830f14397e86e3d1e181c6ad23))
* **deps:** update plugin publishoncentral to v5.0.24 ([728cfda](https://github.com/Collektive/collektive/commit/728cfdac958a8b6072b07351b40da453c969bdaa))


### General maintenance

* use correct task name ([cb6086e](https://github.com/Collektive/collektive/commit/cb6086e03f1d930c6fec22742ae907d3c91b536d))

## [7.0.6](https://github.com/Collektive/collektive/compare/7.0.5...7.0.6) (2024-03-04)


### Dependency updates

* **deps:** update dependency io.arrow-kt:arrow-core to v1.2.2 ([0b5c2aa](https://github.com/Collektive/collektive/commit/0b5c2aaf306649436c451edec10e7dc394b08c23))
* **deps:** update dependency io.arrow-kt:arrow-core to v1.2.3 ([016e85e](https://github.com/Collektive/collektive/commit/016e85e3369bb8f330dd1a9f5a83566dfcee049a))


### Documentation

* **deps:** update dependency org.jetbrains.dokka to v1.9.20 ([e79a022](https://github.com/Collektive/collektive/commit/e79a02222a7747ead880a1b5a12560777b227529))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.3 ([c778ff4](https://github.com/Collektive/collektive/commit/c778ff4efcfcabfe7db44aae5aee98ecb577dba3))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.4 ([f47eb29](https://github.com/Collektive/collektive/commit/f47eb29629da83a0aed0fcf1ba85369023ec7b21))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.5 ([3ee71c7](https://github.com/Collektive/collektive/commit/3ee71c708f1134cebef313758a6c9baf2894598f))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.6 ([4a27e1a](https://github.com/Collektive/collektive/commit/4a27e1aa728a56ee93e1a3b92593e52189a23a6b))

## [7.0.5](https://github.com/Collektive/collektive/compare/7.0.4...7.0.5) (2024-02-17)


### Dependency updates

* **core-deps:** update plugin kover to v0.7.6 ([95162bf](https://github.com/Collektive/collektive/commit/95162bf268f4cae19057bb64cabe4831ab3460ce))

## [7.0.4](https://github.com/Collektive/collektive/compare/7.0.3...7.0.4) (2024-02-15)


### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.8.0 ([b655fee](https://github.com/Collektive/collektive/commit/b655feeb3a575f31e4fa3a945bb687f58e17492f))
* **deps:** update alchemist to v30.1.11 ([e6344ef](https://github.com/Collektive/collektive/commit/e6344ef7ea0efc9206f86da53898bed75f6cabd3))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.1 ([891f2b3](https://github.com/Collektive/collektive/commit/891f2b3be5b996be0d8b1054f825e65fd8fc5fcd))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.2 ([ced20fc](https://github.com/Collektive/collektive/commit/ced20fc985ae7f61bd5fb65ae4cd0bb96b186c35))

## [7.0.3](https://github.com/Collektive/collektive/compare/7.0.2...7.0.3) (2024-02-10)


### Dependency updates

* **deps:** update alchemist ([604a4ef](https://github.com/Collektive/collektive/commit/604a4efeafba25750a3f66b41d0571d7348f5301))
* **deps:** update alchemist to v30.1.10 ([e07a9d9](https://github.com/Collektive/collektive/commit/e07a9d93014d77dd324f2c471b27bd64681521aa))
* **deps:** update alchemist to v30.1.2 ([e2d033f](https://github.com/Collektive/collektive/commit/e2d033f062005e08494b561ff4324dc1673508ba))
* **deps:** update alchemist to v30.1.3 ([6fa8690](https://github.com/Collektive/collektive/commit/6fa86905714cf9f58beb1fce75931c5fad21f10f))
* **deps:** update alchemist to v30.1.8 ([8429dce](https://github.com/Collektive/collektive/commit/8429dcec31d595cbdcb5110ffb4d7799fea49ce3))
* **deps:** update alchemist to v30.1.9 ([28cabd8](https://github.com/Collektive/collektive/commit/28cabd85aeba2bfb7b029ceb80790c67c943d81a))
* **deps:** update plugin kotlin-qa to v0.60.0 ([47088c8](https://github.com/Collektive/collektive/commit/47088c86f16445b3bf5bf931940a8ebd16f04089))
* **deps:** update plugin kotlin-qa to v0.60.1 ([05a378e](https://github.com/Collektive/collektive/commit/05a378ec77ec0e8cac79e2a118e5575680a6bdb1))


### Bug Fixes

* overloading functions now generate different path based on the function' signature ([#232](https://github.com/Collektive/collektive/issues/232)) ([10bd32b](https://github.com/Collektive/collektive/commit/10bd32ba5645651c1360c22af14badcc49c9cc57))


### Build and continuous integration

* **deps:** update actions/setup-node action to v4.0.2 ([9bd0c7f](https://github.com/Collektive/collektive/commit/9bd0c7f06b022f1615a503364cba25dcbffc2c6a))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.4.0 ([9f6b4a5](https://github.com/Collektive/collektive/commit/9f6b4a55c7b7912276d122359c215b94ce48c72c))

## [7.0.2](https://github.com/Collektive/collektive/compare/7.0.1...7.0.2) (2024-02-06)


### Bug Fixes

* use `exchanging` to send the same value to all the neighbors solving a problem where the same initial value is always sent when multiple call occurs ([#224](https://github.com/Collektive/collektive/issues/224)) ([124d8ac](https://github.com/Collektive/collektive/commit/124d8ac040f07c3136d43f881e8fb0683e0ba4a2))

## [7.0.1](https://github.com/Collektive/collektive/compare/7.0.0...7.0.1) (2024-02-05)


### Dependency updates

* **deps:** update alchemist to v30.0.5 ([0cf2973](https://github.com/Collektive/collektive/commit/0cf29733dc6adec5a96a992c50511935e8ef8748))
* **deps:** update dependency gradle to v8.6 ([8da0fd3](https://github.com/Collektive/collektive/commit/8da0fd3b87c56e312d62603150bf2c7f27988683))
* **deps:** update plugin gitsemver to v3.1.1 ([3f98223](https://github.com/Collektive/collektive/commit/3f98223f3887cef2eb6d39698eb4fb18e70aadca))
* **deps:** update plugin kotlin-qa to v0.59.1 ([75ff12b](https://github.com/Collektive/collektive/commit/75ff12b22f19a3dc730f5b9f44332f1eec43ac68))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2.0.1 ([4e4c9de](https://github.com/Collektive/collektive/commit/4e4c9de431c66c691aaa9b38171178bad3325ace))
* **deps:** update plugin publishoncentral to v5.0.23 ([9141b6b](https://github.com/Collektive/collektive/commit/9141b6b6d2232da9b7469c133c213af55d453b7f))


### Performance improvements

* exchange optimization on outbound message when a field with the same values for all the neighbors should be sent ([#225](https://github.com/Collektive/collektive/issues/225)) ([7df12f6](https://github.com/Collektive/collektive/commit/7df12f6a8f55efd56e6cfc7535d563cd185dbd15))


### Tests

* add test which emulates the error thrown in the simulation (issue [#207](https://github.com/Collektive/collektive/issues/207)) ([#208](https://github.com/Collektive/collektive/issues/208)) ([ca65c7e](https://github.com/Collektive/collektive/commit/ca65c7e88a666d1f988868793be52316eaa1ea1a))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.20 ([7429e4e](https://github.com/Collektive/collektive/commit/7429e4e31d5c6bde2b78e75104aceeeaa583da6e))

## [7.0.0](https://github.com/Collektive/collektive/compare/6.1.1...7.0.0) (2024-02-02)


### ⚠ BREAKING CHANGES

* move `YieldingResult` off `YieldingContext` and write a nicer `sharing` (#215)

### Refactoring

* move `YieldingResult` off `YieldingContext` and write a nicer `sharing` ([#215](https://github.com/Collektive/collektive/issues/215)) ([1901044](https://github.com/Collektive/collektive/commit/1901044afaf5b9c26a67d637843767e8e0120793))

## [6.1.1](https://github.com/Collektive/collektive/compare/6.1.0...6.1.1) (2024-02-02)


### Dependency updates

* **deps:** update alchemist to v30.0.4 ([3073566](https://github.com/Collektive/collektive/commit/307356660c8782b2b51621ed67f7e7e356318e73))
* **deps:** update plugin detekt to v1.23.5 ([77df53e](https://github.com/Collektive/collektive/commit/77df53e1710ff07ef3381992357dbdae769fafa9))
* **deps:** update plugin kotlin-qa to v0.59.0 ([458cfe4](https://github.com/Collektive/collektive/commit/458cfe41808c30eed53df2d7ebf0f68a0cd9d835))
* **deps:** update plugin multijvmtesting to v0.5.8 ([0b86414](https://github.com/Collektive/collektive/commit/0b86414129055455b48d2290ca52d4d00bb98117))


### Bug Fixes

* solve an alignment issue preventing the right alignment of nested branch conditions ([#216](https://github.com/Collektive/collektive/issues/216)) ([1f514b9](https://github.com/Collektive/collektive/commit/1f514b99fd31354988ab323383cb74fc3f79f051))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.16 ([ced754a](https://github.com/Collektive/collektive/commit/ced754aeb89ada4c4ac9e58951efa06f4db75138))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.17 ([8da39c2](https://github.com/Collektive/collektive/commit/8da39c2e783422bace92f1e74f19dd5152fcc4e6))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.18 ([29801f8](https://github.com/Collektive/collektive/commit/29801f8188b6696ea7ca04df7e9d62a568af3821))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.19 ([83c8695](https://github.com/Collektive/collektive/commit/83c8695d9c5ec63e5ecd5fb369a3e86dc664a849))

## [6.1.0](https://github.com/Collektive/collektive/compare/6.0.0...6.1.0) (2024-01-31)


### Features

* **alchemist-incarnation:** add support for Kotlin-interpreted properties ([#204](https://github.com/Collektive/collektive/issues/204)) ([b23a25e](https://github.com/Collektive/collektive/commit/b23a25efb6053a77a1ab53a82b3e224450f315aa))


### Dependency updates

* **deps:** update alchemist to v30.0.3 ([74ade19](https://github.com/Collektive/collektive/commit/74ade19cf5719450db48ce7d6a7b1bb50304118b))

## [6.0.0](https://github.com/Collektive/collektive/compare/5.1.0...6.0.0) (2024-01-30)


### ⚠ BREAKING CHANGES

* drop `ID` and revise several parts of the API (#179)

### Dependency updates

* **deps:** update alchemist to v30 ([718d5d8](https://github.com/Collektive/collektive/commit/718d5d845ea100c547c3ba3f17c9c6b198afe881))
* **deps:** update alchemist to v30.0.1 ([69353af](https://github.com/Collektive/collektive/commit/69353afdc6da87fe40f229814e153a4fef273779))
* **deps:** update alchemist to v30.0.2 ([77c6d71](https://github.com/Collektive/collektive/commit/77c6d71cc293f689eec61e80ab6f6d4fce7812b0))
* **deps:** update plugin com.gradle.enterprise to v3.16.2 ([824311a](https://github.com/Collektive/collektive/commit/824311a05f6d737fb45dbbb810b6201e50349975))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.15 ([9f7f6c0](https://github.com/Collektive/collektive/commit/9f7f6c07999365e8bf94de83acaf1a9ae8153811))


### Refactoring

* drop `ID` and revise several parts of the API ([#179](https://github.com/Collektive/collektive/issues/179)) ([ecca283](https://github.com/Collektive/collektive/commit/ecca283aa3d5e49d2dbb40a2894d20589be2d387))

## [5.1.0](https://github.com/Collektive/collektive/compare/5.0.0...5.1.0) (2024-01-22)


### Features

* add `alchemist-incarnation-collektive`, implementing an Alchemist incarnation to run Collektive in the simulator ([#193](https://github.com/Collektive/collektive/issues/193)) ([fee291e](https://github.com/Collektive/collektive/commit/fee291e4995d4c90e284c1cbdc8ac0f306b668e8))


### Dependency updates

* **deps:** update alchemist to v29.5.0 ([c3916bc](https://github.com/Collektive/collektive/commit/c3916bce50278b5c56ecd4d1ce38d4a05b34d222))
* **deps:** update dependency it.unibo.alchemist:alchemist-api to v29.5.1 ([39b3dd8](https://github.com/Collektive/collektive/commit/39b3dd8be5aca97987f142067eab589107047a9b))
* **deps:** update plugin gitsemver to v3 ([86849cd](https://github.com/Collektive/collektive/commit/86849cd8ee65b40cf0f38c957afa126f352a5cf9))
* **deps:** update plugin gitsemver to v3.1.0 ([37f789a](https://github.com/Collektive/collektive/commit/37f789a8c7fe833228c67313c65a74de7454356b))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.14 ([1c15691](https://github.com/Collektive/collektive/commit/1c156914860db1985d14fb83b02a14c4eabcaa10))

## [5.0.0](https://github.com/Collektive/collektive/compare/4.0.0...5.0.0) (2024-01-16)


### ⚠ BREAKING CHANGES

* introduce the `exchanging` function to be consistent with `sharing` and `repeating` and refactor implementation (#177)

### Features

* introduce the `exchanging` function to be consistent with `sharing` and `repeating` and refactor implementation ([#177](https://github.com/Collektive/collektive/issues/177)) ([2bf33d9](https://github.com/Collektive/collektive/commit/2bf33d90627d72e71093c17fce271cbb647edc29)), closes [#190](https://github.com/Collektive/collektive/issues/190)


### Dependency updates

* **deps:** update alchemist to v29.3.5 ([4db7363](https://github.com/Collektive/collektive/commit/4db7363ef746c4d20fb2db4c573b2ff3aa62b793))
* **deps:** update alchemist to v29.4.0 ([a14609a](https://github.com/Collektive/collektive/commit/a14609ab3b8453fb39f23b383ac5b27afb9c5f13))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.3.5 ([52b5c64](https://github.com/Collektive/collektive/commit/52b5c643541067f11b1eb06772284904e66afdc0))
* **deps:** update node.js to 20.11 ([bf1ff85](https://github.com/Collektive/collektive/commit/bf1ff854446078c993ada5fd978749a754bdf141))
* **deps:** update plugin kotlin-qa to v0.58.0 ([33e774d](https://github.com/Collektive/collektive/commit/33e774dbaf0edfbb4d9cf215fb3d81a7d43410b7))
* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.8.0 ([69bffbd](https://github.com/Collektive/collektive/commit/69bffbde6d519da27a0feed11b6366d22a8ecdaa))


### Tests

* **dsl:** aligment fails clearly when a value is sent multiple times with the same path ([#185](https://github.com/Collektive/collektive/issues/185)) ([8e383fd](https://github.com/Collektive/collektive/commit/8e383fdf7ed51cb58d7bde7b2372eb53bb2610a0))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.13 ([89f1db0](https://github.com/Collektive/collektive/commit/89f1db09e84c64f789d62f7fe5915554c6d116a8))

## [4.0.0](https://github.com/Collektive/collektive/compare/3.0.1...4.0.0) (2024-01-08)


### ⚠ BREAKING CHANGES

* implement rule `[E-FLD]` as described in https://doi.org/10.1145/3285956. Fixes #171 (#172)

### Dependency updates

* **deps:** update alchemist to v29.3.4 ([c2090e9](https://github.com/Collektive/collektive/commit/c2090e96090f611587ee6e09a1a5f3756c12feaa))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.3.2 ([fd5efbf](https://github.com/Collektive/collektive/commit/fd5efbf0b577cba3c2eaf4fccc0060a335eb9288))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.3.3 ([01573c4](https://github.com/Collektive/collektive/commit/01573c4d029189552b42f594f47ffd5b73a2501b))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.85 ([312363b](https://github.com/Collektive/collektive/commit/312363b95311d2d7f9790879157ef37abc507728))
* **deps:** update plugin gitsemver to v2.0.5 ([f654e59](https://github.com/Collektive/collektive/commit/f654e59d8cfe2aad197ba98c70ea7bc85f58dcf1))
* **deps:** update plugin kotlin-qa to v0.57.1 ([5c4a72b](https://github.com/Collektive/collektive/commit/5c4a72bb80e8283e3b98e5792de8599fa1cf1eda))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.17 ([9b9382f](https://github.com/Collektive/collektive/commit/9b9382f3b419e9cb8b05a8a6a2d119f29744ee80))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v2 ([eb03b09](https://github.com/Collektive/collektive/commit/eb03b09802f59d51139ea0742a3902da834941d2))
* **deps:** update plugin publishoncentral to v5.0.22 ([5627bab](https://github.com/Collektive/collektive/commit/5627bab0075ef00716e240a96bda18eda0d97094))


### Bug Fixes

* implement rule `[E-FLD]` as described in https://doi.org/10.1145/3285956. Fixes [#171](https://github.com/Collektive/collektive/issues/171) ([#172](https://github.com/Collektive/collektive/issues/172)) ([bb70be2](https://github.com/Collektive/collektive/commit/bb70be229bf9a1afb81f584eb226e641b3acbafc))

## [3.0.1](https://github.com/Collektive/collektive/compare/3.0.0...3.0.1) (2023-12-21)


### Dependency updates

* **core-deps:** update kotlin monorepo to v1.9.22 ([ffee000](https://github.com/Collektive/collektive/commit/ffee0000c2dcec516535e560a683f2b16e022aec))
* **deps:** update alchemist to v29.0.7 ([d8d9241](https://github.com/Collektive/collektive/commit/d8d9241dd6179e3df64c259832b2f648bed5243f))
* **deps:** update alchemist to v29.1.0 ([8e014ab](https://github.com/Collektive/collektive/commit/8e014ab4b1cf945788ccbab1a552c8b3d232a23d))
* **deps:** update alchemist to v29.1.1 ([9e7d929](https://github.com/Collektive/collektive/commit/9e7d929fd5fde5454c952751b4c15778e2ad000f))
* **deps:** update alchemist to v29.2.0 ([bbed5f6](https://github.com/Collektive/collektive/commit/bbed5f675da2c53387cb522509383c35ac2a89c9))
* **deps:** update alchemist to v29.3.0 ([0d77894](https://github.com/Collektive/collektive/commit/0d778942576be0b2d3eea6ca64a3c15eabd31f24))
* **deps:** update alchemist to v29.3.1 ([ddc1a29](https://github.com/Collektive/collektive/commit/ddc1a29e4c915b667f78708c4e6017bf2c6d698f))
* **deps:** update alchemist to v29.3.2 ([d45371b](https://github.com/Collektive/collektive/commit/d45371b5070527a93c5dd93af744b6fb39151944))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5 ([619cd55](https://github.com/Collektive/collektive/commit/619cd55dcd4b6a382128fe3991dac18d2aab6c24))
* **deps:** update dependency com.github.gmazzo.buildconfig to v5.0.1 ([40b8323](https://github.com/Collektive/collektive/commit/40b832369693257b59be4d4a032b83332df2dab7))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.80 ([7ed40bc](https://github.com/Collektive/collektive/commit/7ed40bc8980cb6da61d871159fafac5b3eb1547a))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.81 ([7440ba4](https://github.com/Collektive/collektive/commit/7440ba442374ab0ef01b94fb5389d09ba27b55bf))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.82 ([82fac18](https://github.com/Collektive/collektive/commit/82fac1859f98bba9e46c7057c29a52a3024e87cd))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.83 ([39ae86a](https://github.com/Collektive/collektive/commit/39ae86a593530955374bd59c4d4eb26970340f00))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.84 ([bfa62b0](https://github.com/Collektive/collektive/commit/bfa62b004f7a1adbf3c88987399d56c5078d6001))
* **deps:** update plugin com.gradle.enterprise to v3.16.1 ([0693294](https://github.com/Collektive/collektive/commit/069329464441d5a15e86b8a4b34fbe1f73d58802))
* **deps:** update plugin kotlin-qa to v0.57.0 ([20e5eda](https://github.com/Collektive/collektive/commit/20e5edab6b30e1344db76d47eabd041bfafea925))


### Build and continuous integration

* **deps:** update actions/setup-node action to v4.0.1 ([7262acf](https://github.com/Collektive/collektive/commit/7262acf624d05d3c71acae1b17867930ec75703a))


### Refactoring

* simplify the alignment logic ([#145](https://github.com/Collektive/collektive/issues/145)) ([cfb479d](https://github.com/Collektive/collektive/commit/cfb479db0797c80f236785b6ed1286c00d40cb01))

## [3.0.0](https://github.com/Collektive/collektive/compare/2.1.3...3.0.0) (2023-12-07)


### ⚠ BREAKING CHANGES

* introduce Collektive entrypoint (#142)

### Features

* introduce Collektive entrypoint ([#142](https://github.com/Collektive/collektive/issues/142)) ([8bf8517](https://github.com/Collektive/collektive/commit/8bf8517edbdde8611d1bd99991abc72d4410513a))


### Dependency updates

* **deps:** update alchemist to v29 ([ce990da](https://github.com/Collektive/collektive/commit/ce990da0fc75ea85b1825cf56145671ff6acc3d0))
* **deps:** update dependency gradle to v8.5 ([a18e39b](https://github.com/Collektive/collektive/commit/a18e39b652681d69108cf7f89d7be60b516437a1))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.79 ([b58fd5a](https://github.com/Collektive/collektive/commit/b58fd5a5955d643cf067271dcf8bf5674eebad97))
* **deps:** update plugin com.gradle.enterprise to v3.16 ([0bb24d6](https://github.com/Collektive/collektive/commit/0bb24d694baaa4e69ab837cec35804c9d6815add))
* **deps:** update plugin gitsemver to v2.0.4 ([29c2bae](https://github.com/Collektive/collektive/commit/29c2bae51b456a41514af67a1683760758a82e04))
* **deps:** update plugin kotlin-qa to v0.55.2 ([9d22b7a](https://github.com/Collektive/collektive/commit/9d22b7a389fc1f397a5e6e072dcef87b5eb20e63))
* **deps:** update plugin kotlin-qa to v0.56.0 ([7849958](https://github.com/Collektive/collektive/commit/7849958d25f589e13ae6e866c69347212966e654))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.16 ([215292e](https://github.com/Collektive/collektive/commit/215292e565f7dad34f960ea0826d0c999f0b6bcf))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.12 ([c9e1d72](https://github.com/Collektive/collektive/commit/c9e1d728fbe3ccd4615ab66db4784b4710218ebb))

## [2.1.3](https://github.com/Collektive/collektive/compare/2.1.2...2.1.3) (2023-11-29)


### Dependency updates

* **core-deps:** update plugin kover to v0.7.5 ([52cb126](https://github.com/Collektive/collektive/commit/52cb126f545891301b6afab6d8dd277b5f53ee17))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.78 ([79a7d6f](https://github.com/Collektive/collektive/commit/79a7d6f0f4e66db068e725264b897a7ef33b848c))
* **deps:** update plugin detekt to v1.23.4 ([362f618](https://github.com/Collektive/collektive/commit/362f618038547a79531f92523d69affd422beb0e))
* **deps:** update plugin gitsemver to v2.0.3 ([b033d42](https://github.com/Collektive/collektive/commit/b033d4253f0afe16310f4c30ed8a4039f2abf4ab))
* **deps:** update plugin kotlin-qa to v0.54.1 ([569afe9](https://github.com/Collektive/collektive/commit/569afe9f7413fc40c9e1f3c8ae4a8b69b309523d))
* **deps:** update plugin kotlin-qa to v0.55.1 ([ef43f6a](https://github.com/Collektive/collektive/commit/ef43f6a4926cab09c45da5db18720f0b6abb30dc))
* **deps:** update plugin multijvmtesting to v0.5.7 ([a49737b](https://github.com/Collektive/collektive/commit/a49737b61f4fcfdd0719220b7e25b847227e9157))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.15 ([2f208b0](https://github.com/Collektive/collektive/commit/2f208b0427f29eea08e2f2151a4adfd4a7896e46))
* **deps:** update plugin publishoncentral to v5.0.20 ([3448af8](https://github.com/Collektive/collektive/commit/3448af81a8bf0bc31c5c358320e21c4bb8483537))

## [2.1.2](https://github.com/Collektive/collektive/compare/2.1.1...2.1.2) (2023-11-23)


### Dependency updates

* **core-deps:** update kotlin monorepo to v1.9.21 ([8061420](https://github.com/Collektive/collektive/commit/8061420aab6a3c393806d6dc7017bf9a22d90181))
* **deps:** update dependency com.github.gmazzo.buildconfig to v4.2.0 ([6e3b452](https://github.com/Collektive/collektive/commit/6e3b45261fcf63a2f4aeb70d9578ac2b9386c673))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.77 ([1fc29b5](https://github.com/Collektive/collektive/commit/1fc29b542b252023a835aa366bdf6fff9bf7eb97))
* **deps:** update node.js to 20.10 ([01d795b](https://github.com/Collektive/collektive/commit/01d795ba1fd78c8e9046ac398668b4c6c6b972eb))

## [2.1.1](https://github.com/Collektive/collektive/compare/2.1.0...2.1.1) (2023-11-17)


### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.75 ([93c4ac4](https://github.com/Collektive/collektive/commit/93c4ac4a0b4fc3828ab6fede7e674291f47bca22))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.76 ([33638b0](https://github.com/Collektive/collektive/commit/33638b03a01eb0f28ab77ab8c1b1cb8749b8e82a))
* **deps:** update plugin gitsemver to v2.0.2 ([33293d2](https://github.com/Collektive/collektive/commit/33293d2b63d2507a8d7c5afaabbbc4ee68c7d420))


### Performance improvements

* fast exchange ([#118](https://github.com/Collektive/collektive/issues/118)) ([dc3156d](https://github.com/Collektive/collektive/commit/dc3156d87b5ff16d7ef7aef338d74a6976e8120e))

## [2.1.0](https://github.com/Collektive/collektive/compare/2.0.1...2.1.0) (2023-11-10)


### Features

* rename actual map in mapWithId and add map which not considers the ids ([3c95142](https://github.com/Collektive/collektive/commit/3c9514271cbeb06d54f0b2edb040bbdeb8560e47))


### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.71 ([710fa00](https://github.com/Collektive/collektive/commit/710fa00ab3f9f89c7d09e3bffb88d33d94c34968))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.72 ([cd1e069](https://github.com/Collektive/collektive/commit/cd1e069e9f0cd83219e69f46c5bba4e218ef3b09))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.73 ([8de97b3](https://github.com/Collektive/collektive/commit/8de97b3df12391c4c5bfc32834a146f03b7e8153))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.74 ([21f3d67](https://github.com/Collektive/collektive/commit/21f3d67c412b82f580c260973b2c0c1620c59b0c))
* **deps:** update plugin kotlin-qa to v0.54.0 ([25dc557](https://github.com/Collektive/collektive/commit/25dc5575da0cbb9c7c7548dc79a469b7e291e79d))
* **deps:** update plugin publishoncentral to v5.0.19 ([eb13b70](https://github.com/Collektive/collektive/commit/eb13b7049924258ae81107fadc5f95adea08ed33))


### Bug Fixes

* solve a StackOverflowException in equals on fields ([f73047d](https://github.com/Collektive/collektive/commit/f73047da7fbeb0331c9eca30302a2c76e1a282bf))


### Tests

* add field operation tests ([6d1000e](https://github.com/Collektive/collektive/commit/6d1000e8326e2d2c2010edb72e9e4e56db928c3a))
* test the field using a closing operation insead the returning lazy field ([b596958](https://github.com/Collektive/collektive/commit/b5969589e42c32c2404e0d6d5f20fff4a7a6b1f4))


### Build and continuous integration

* fix license and developers for maven pubblication ([7ace8cb](https://github.com/Collektive/collektive/commit/7ace8cb1790b787230d1ca548a69f5c32ad885b8))
* revert to non-K2 language version and re-enable allWarningsAsErrors = true ([946097d](https://github.com/Collektive/collektive/commit/946097deb4f334e7e77329054ef54000353b5d73))
* revert to non-K2 the compiler plugin ([9f64b06](https://github.com/Collektive/collektive/commit/9f64b0655109b9c868180df3a90711a9ae5464d4))
* set jvm target to 1.8 ([bb60c6e](https://github.com/Collektive/collektive/commit/bb60c6e397033f537a37df38f845d83d2a33bb71))


### Style improvements

* remove unused imports ([b6a7036](https://github.com/Collektive/collektive/commit/b6a7036719499e678f93f3ffdad7b9d66e5ef759))


### Refactoring

* implement cache-based field implementation ([9658d00](https://github.com/Collektive/collektive/commit/9658d00ccd8efa86440645d325f2303efe7bf15b))
* new field implementation ([67f136e](https://github.com/Collektive/collektive/commit/67f136e812915ae580c41cd47eeee5723aa2c5b4))
* remove smart contructor with list ([148c04c](https://github.com/Collektive/collektive/commit/148c04cba5a81b09e35253d4af73bb55348d2c79))
* use lazy field when mapping ([dd85ba8](https://github.com/Collektive/collektive/commit/dd85ba820bbb52c645f003bc26666793788af095))

## [2.0.1](https://github.com/Collektive/collektive/compare/2.0.0...2.0.1) (2023-11-04)


### Dependency updates

* **core-deps:** update kotlin monorepo to v1.9.20 ([f4ce9bb](https://github.com/Collektive/collektive/commit/f4ce9bbc193bbc8c9b51ce11001dcbb87b475810))
* **deps:** add kotlin gradle api in the catalog ([870d3bc](https://github.com/Collektive/collektive/commit/870d3bc5b842f5553f25aaf3a6dced6ec3d931b2))
* **deps:** update dependency io.arrow-kt:arrow-core to v1.2.1 ([8675ba4](https://github.com/Collektive/collektive/commit/8675ba4c73763741af3622b2e656bf815c556de6))
* **deps:** update kotest to v5.8.0 ([7c5d415](https://github.com/Collektive/collektive/commit/7c5d4157795e3dab8c76b0de676b1d6e269c023d))


### Bug Fixes

* get alignOn function direclty from the AggregateContext class ([4f9507b](https://github.com/Collektive/collektive/commit/4f9507b3d2bea8ff574a8efb50a7a6627941deb7))
* simplify branch representation for the alignemnt adpating to new K2 representation ([ba4fd08](https://github.com/Collektive/collektive/commit/ba4fd08d9db4fda653b12ece9bc367a9ce249849))
* use new k2 compiler api ([c36561c](https://github.com/Collektive/collektive/commit/c36561c08d63c7b9a3d2755190529d3ee3c0dc8d))


### Build and continuous integration

* add arrow dependency ([5d04552](https://github.com/Collektive/collektive/commit/5d045520855867427f11795f8dff5a79859bcbf9))
* apply collektive plugin to all (future) subprojects and not only to 'dsl' ([20b850f](https://github.com/Collektive/collektive/commit/20b850f8865e36a8cb625856256b0bdf7c41841d))
* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.11 ([8e3caee](https://github.com/Collektive/collektive/commit/8e3caeece78608b9e35e7b85095c2c7ab8691ebc))
* enable default hierarchy template and remove deprecated targets ([e0a875b](https://github.com/Collektive/collektive/commit/e0a875b1401d650ab446638c8d45a6379bcd8f32))
* enable k2 and minor gradle project refactoring ([49cb1b1](https://github.com/Collektive/collektive/commit/49cb1b1a9695ab2300f44bf0daa7b661da41ee11))
* use catalog dependencies ([1d72f2f](https://github.com/Collektive/collektive/commit/1d72f2f1adf8904b22bb79daf30a4f06a68e3599))

## [2.0.0](https://github.com/Collektive/collektive/compare/1.0.0...2.0.0) (2023-11-03)


### ⚠ BREAKING CHANGES

* update usage and functioning of old "butReturn" into "yielding" in share function (#90)

### Features

* update usage and functioning of old "butReturn" into "yielding" in share function ([#90](https://github.com/Collektive/collektive/issues/90)) ([d7e8c7f](https://github.com/Collektive/collektive/commit/d7e8c7ff1cf830ad4a4bff1ef8747d9c7d450c4c))


### Dependency updates

* **deps:** update dependency io.gitlab.arturbosch.detekt:detekt-formatting to v1.23.2 ([9f4b5a8](https://github.com/Collektive/collektive/commit/9f4b5a888c4d3cb42b9b7a1e8780be353bdc5abb))
* **deps:** update dependency io.gitlab.arturbosch.detekt:detekt-formatting to v1.23.3 ([786ab0c](https://github.com/Collektive/collektive/commit/786ab0c5f266eaba666134a93642a82b5ed237a4))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.70 ([7e07f66](https://github.com/Collektive/collektive/commit/7e07f665d891f5af7964a5154f1c219e02035ad4))
* **deps:** update plugin detekt to v1.23.2 ([a13f8c6](https://github.com/Collektive/collektive/commit/a13f8c62dd1870c60cd075341fe3175a503a07c3))
* **deps:** update plugin detekt to v1.23.3 ([f227bc6](https://github.com/Collektive/collektive/commit/f227bc6ed35b2c8b2631ca61bf319cd2554b1e8e))
* **deps:** update plugin gitsemver to v2.0.1 ([b4357f2](https://github.com/Collektive/collektive/commit/b4357f29fe625cd21774ca9583bb285b6986f394))
* **deps:** update plugin kotlin-qa to v0.25.1 ([3ffd0a7](https://github.com/Collektive/collektive/commit/3ffd0a755553fabaed26628ee0f7e8e5746711ab))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.14 ([ad55cd5](https://github.com/Collektive/collektive/commit/ad55cd5acb49e153bab871a857570d0768b998bc))
* **deps:** update plugin publishoncentral to v5.0.17 ([25e59c3](https://github.com/Collektive/collektive/commit/25e59c3361c3d1b670f845231e64b78c1ba02502))
* **deps:** update plugin publishoncentral to v5.0.18 ([3061639](https://github.com/Collektive/collektive/commit/30616395da9ea82009f419b9df44ca21f6d00a92))


### Build and continuous integration

* switch to gradle-kotlin-qa ([fc929ca](https://github.com/Collektive/collektive/commit/fc929ca9f67c1d6daa0291ac2806a4c91d171574))


### General maintenance

* **license:** create license file ([29a96e4](https://github.com/Collektive/collektive/commit/29a96e4a9382ce128388bc6f0cb12771cf8d26f4))

## [1.0.0](https://github.com/Collektive/collektive/compare/0.4.2...1.0.0) (2023-10-27)


### ⚠ BREAKING CHANGES

* **dsl:** introduce `exchange` as per https://doi.org/10.4230/LIPIcs.ECOOP.2022.20 (#82)

### Features

* **dsl:** introduce `exchange` as per https://doi.org/10.4230/LIPIcs.ECOOP.2022.20 ([#82](https://github.com/Collektive/collektive/issues/82)) ([619c8de](https://github.com/Collektive/collektive/commit/619c8de8464f9add182193fe42ac83026f35edec))


### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.69 ([0c911af](https://github.com/Collektive/collektive/commit/0c911af1a8ab836f09565b4eb0ae9ec617309f77))
* **deps:** update node.js to 20.9 ([4750c74](https://github.com/Collektive/collektive/commit/4750c74a228c467b95563b6d2198e11ed5a0b837))
* **deps:** update node.js to v20 ([c9e7b35](https://github.com/Collektive/collektive/commit/c9e7b354ef126882f717be0c233698ae5b895bf0))
* **deps:** update plugin gitsemver to v1.1.11 ([1008d9c](https://github.com/Collektive/collektive/commit/1008d9cb3119604638bf08a1cf973c252e7f24a9))
* **deps:** update plugin gitsemver to v1.1.13 ([ac8e475](https://github.com/Collektive/collektive/commit/ac8e475346fd03ed7ba51a25cfaf70d74f190b18))
* **deps:** update plugin gitsemver to v1.1.14 ([70d8942](https://github.com/Collektive/collektive/commit/70d894205cb487f8d7a08025e22e2316a305b065))
* **deps:** update plugin gitsemver to v1.1.15 ([09d0c9a](https://github.com/Collektive/collektive/commit/09d0c9a187617adf92300099aa14c5ceb357f1a3))
* **deps:** update plugin gitsemver to v2 ([5919aae](https://github.com/Collektive/collektive/commit/5919aaee6f347ed0386143baa6cbcf813960df09))
* **deps:** update plugin multijvmtesting to v0.5.6 ([b1850ae](https://github.com/Collektive/collektive/commit/b1850aea8dbbf14ba3812880f07a59e2474111c3))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.11 ([f071214](https://github.com/Collektive/collektive/commit/f07121421a4e18b2181695d69487fde75083f6fa))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.13 ([6b78501](https://github.com/Collektive/collektive/commit/6b7850134122828b94d7aec6a1ec63aeaa4f3f83))
* **deps:** update plugin publishoncentral to v5.0.16 ([61578e5](https://github.com/Collektive/collektive/commit/61578e5dd4959544c9d44bf7d72527d388ccdda1))


### Build and continuous integration

* **deps:** update actions/checkout action to v4.1.1 ([d7efed8](https://github.com/Collektive/collektive/commit/d7efed8a6a52eba05dc03e4ad727bc8bfe103645))
* **deps:** update actions/setup-node action to v3.8.2 ([7dac9fb](https://github.com/Collektive/collektive/commit/7dac9fb0fcf22deb2b200060c5f8e031ce2cb2a5))
* **deps:** update actions/setup-node action to v4 ([6fa0abd](https://github.com/Collektive/collektive/commit/6fa0abd78a2f3950baca57a6aaf981c0884ae0ff))

## [0.4.2](https://github.com/Collektive/collektive/compare/0.4.1...0.4.2) (2023-10-16)


### Documentation

* **deps:** update dependency org.jetbrains.dokka to v1.9.10 ([2c5cc71](https://github.com/Collektive/collektive/commit/2c5cc715a26befee27162248c28f49d95c97d4e8))

## [0.4.1](https://github.com/Collektive/collektive/compare/0.4.0...0.4.1) (2023-10-11)


### Dependency updates

* **core-deps:** update plugin kover to v0.7.4 ([bc7ad96](https://github.com/Collektive/collektive/commit/bc7ad9612a923ced3c4f906a97579c557f62c4c1))

## [0.4.0](https://github.com/Collektive/collektive/compare/0.3.4...0.4.0) (2023-10-08)


### Features

* add operations between fields ([8b26692](https://github.com/Collektive/collektive/commit/8b266923340af739d81b3a62fa1c93ed13a0c360))
* implement conversion from map to field ([a6fb9b7](https://github.com/Collektive/collektive/commit/a6fb9b780b3b2ddcc18e2d5a0d12f1b48ef1baba))


### Dependency updates

* **deps:** update dependency gradle to v8.4 ([d851cd8](https://github.com/Collektive/collektive/commit/d851cd82a29a6abb26f14de874f56b92f87971dd))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.64 ([407bf2b](https://github.com/Collektive/collektive/commit/407bf2b69405afe15efef84107f8b1be3f284bed))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.65 ([dcc5f3e](https://github.com/Collektive/collektive/commit/dcc5f3ec81ddd5e41115939b4b3e56f3c71db20e))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.66 ([0f6d5d3](https://github.com/Collektive/collektive/commit/0f6d5d3f57aea31cbdabf3af8de81412e063eff3))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.67 ([b062a76](https://github.com/Collektive/collektive/commit/b062a76c2269802875144d8fe6625b896f78409e))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.68 ([3177127](https://github.com/Collektive/collektive/commit/3177127910287357edfeb30572b7b8cd15fbcbac))
* **deps:** update plugin com.gradle.enterprise to v3.15.1 ([8f3e002](https://github.com/Collektive/collektive/commit/8f3e0021dd642079572eea70c4d7bea83c229055))
* **deps:** update plugin publishoncentral to v5.0.15 ([182ff0e](https://github.com/Collektive/collektive/commit/182ff0ebe119ec4009c379f4921a1a144a4c4a1d))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.10 ([1c0c597](https://github.com/Collektive/collektive/commit/1c0c597bc2637384210e01202324d1963daec3be))

## [0.3.4](https://github.com/Collektive/collektive/compare/0.3.3...0.3.4) (2023-09-29)


### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.63 ([318b76c](https://github.com/Collektive/collektive/commit/318b76c2d4fc079f38ea2dc5956c7221aebf662d))


### Bug Fixes

* use a different branch representation on alignment ([9b57ed0](https://github.com/Collektive/collektive/commit/9b57ed0f51239bd652655c15267058cd217d23e8))


### Tests

* adjust tests according to new branch representation ([68d9e19](https://github.com/Collektive/collektive/commit/68d9e196782ac56d4d6b5509b61ec369ea4a2a9a))


### General maintenance

* set line lenght to 150 ([70c6c1c](https://github.com/Collektive/collektive/commit/70c6c1cd1e69aec9b8edd40df7c5e12107742b27))

## [0.3.3](https://github.com/Collektive/collektive/compare/0.3.2...0.3.3) (2023-09-28)


### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.56 ([a8de136](https://github.com/Collektive/collektive/commit/a8de1362dc004491e5cf90c8247dd75bceeeb916))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.58 ([62b4059](https://github.com/Collektive/collektive/commit/62b405971ff11ee03316042bfcfd0d30cf0bb6df))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.59 ([10e32e1](https://github.com/Collektive/collektive/commit/10e32e1d6424bbd59443865a17a1065c28122f41))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.60 ([93aa6ac](https://github.com/Collektive/collektive/commit/93aa6ac00bae63e3d58f1d198c922cb39c5bc161))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.61 ([64230fc](https://github.com/Collektive/collektive/commit/64230fc0009235b446368659b84b938b1e3f1eaa))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.62 ([ce2436d](https://github.com/Collektive/collektive/commit/ce2436d804fa988b0efb74583d037055edca734a))


### Bug Fixes

* branch alignment ([ec9c440](https://github.com/Collektive/collektive/commit/ec9c440b8285b8d815ced36801062a65faffb343))
* use a counter to distingush the same operator used in different position. Issue [#51](https://github.com/Collektive/collektive/issues/51) ([78d9634](https://github.com/Collektive/collektive/commit/78d96345c2cb99d897a4cbab7c7edd0289493a6e))


### Tests

* add regression test for issue [#51](https://github.com/Collektive/collektive/issues/51) ([4bc2005](https://github.com/Collektive/collektive/commit/4bc2005336141e07bc33445add0c7e39ad7cbc47))


### Build and continuous integration

* **deps:** update actions/checkout action to v4.1.0 ([a526160](https://github.com/Collektive/collektive/commit/a526160a2794bb273ffcaef18479d7590aef40ba))


### General maintenance

* add logging dependency ([b75cdce](https://github.com/Collektive/collektive/commit/b75cdce7eed93cfdc0b11dd7adab247bd10f5eb6))


### Style improvements

* minor style improvements ([83b15dd](https://github.com/Collektive/collektive/commit/83b15ddfe656852bff690d0883346f3a4ed0bed0))

## [0.3.2](https://github.com/Collektive/collektive/compare/0.3.1...0.3.2) (2023-09-19)


### Dependency updates

* **deps:** update node.js to 18.18 ([9dfa73e](https://github.com/Collektive/collektive/commit/9dfa73e65b0e71a1d645c4f09a5a894d4d522f13))


### Bug Fixes

* add logger to compiler plugin ([f92aeab](https://github.com/Collektive/collektive/commit/f92aeabbaf0e4b436bf055c4f7fbb42a15df85b8))


### General maintenance

* remove alchemist incarnation as moved on a separate repo ([9fa122e](https://github.com/Collektive/collektive/commit/9fa122e115d4757e5d4d803bd124bd3fce9bbd99))


### Refactoring

* remove uneeded function ([006e426](https://github.com/Collektive/collektive/commit/006e42633ad804a146f10a1bf60921423a5a0a14))

## [0.3.1](https://github.com/Collektive/collektive/compare/0.3.0...0.3.1) (2023-09-18)


### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.55 ([14a5abd](https://github.com/Collektive/collektive/commit/14a5abd1e27a828a906f7455b07087ae1c7a751e))
* **deps:** update plugin publishoncentral to v5.0.14 ([f89c4a6](https://github.com/Collektive/collektive/commit/f89c4a6abae8cdf259353450bcf844468f402026))


### Documentation

* add missing KDoc ([a4dc9b7](https://github.com/Collektive/collektive/commit/a4dc9b76ad64cb6de43b76499bfd9eedd90333f9))


### Build and continuous integration

* **deps:** update danysk/build-check-deploy-gradle-action action to v2.2.9 ([e905b63](https://github.com/Collektive/collektive/commit/e905b6376db880409e0e0eb21b4824b232a9dd7e))


### General maintenance

* add deteket config file ([c7de07e](https://github.com/Collektive/collektive/commit/c7de07e95ba55767b6ab7eebd5a83cd0e5f11b65))
* **kover:** setup kover for code coverage ([673bc84](https://github.com/Collektive/collektive/commit/673bc842b7c6d35a47481909edc29866f0de225f))
* update README with badges ([84749d9](https://github.com/Collektive/collektive/commit/84749d9a54f3f22afafa7d9178a69ab983ca5dac))
* use collektive renovate config ([5fe1bd4](https://github.com/Collektive/collektive/commit/5fe1bd4317bc8bd98388b5e4547c59c45c6dc1da))

## [0.3.0](https://github.com/Collektive/collektive/compare/0.2.0...0.3.0) (2023-09-15)


### Features

* add generic field ops and change the way a field is built ([b14968e](https://github.com/Collektive/collektive/commit/b14968e30dbce323c1488d1549f868261df381f4))


### Dependency updates

* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.54 ([a73f1fa](https://github.com/Collektive/collektive/commit/a73f1fa3f2d1ebef6bd21f2019725485b2c80ddc))
* **deps:** update plugin com.gradle.enterprise to v3.15 ([a44dbff](https://github.com/Collektive/collektive/commit/a44dbffc5050f303f52521e4cd8ce3838595f174))


### Bug Fixes

* **plugin:** use consistent plugin ID and rename of the gradle dsl entrypoint of the plugin ([21986af](https://github.com/Collektive/collektive/commit/21986af4bd7e529afbd52ea62f7d5067cce59ad9))


### Documentation

* add readme for using the library ([6d5001d](https://github.com/Collektive/collektive/commit/6d5001d8e59d93024bbb5061479dabbf5ca40bb5))
* improve plugin description ([17b148a](https://github.com/Collektive/collektive/commit/17b148ad034bb2afa101eee9ba38f19e0bf4b8d1))


### Build and continuous integration

* **deps:** update actions/checkout action to v4 ([56f69ae](https://github.com/Collektive/collektive/commit/56f69ae50e85660e4e5f5890a42c27330b68fd32))


### General maintenance

* **build:** rename gradle plugin id ([028e5cb](https://github.com/Collektive/collektive/commit/028e5cb815fe45d53f893a203dfa83c885af87a5))
* use new dsl entrypoint name ([1b7f442](https://github.com/Collektive/collektive/commit/1b7f442d7c8a521f9f3e7d580c8733316a1cc5c7))

## [0.2.0](https://github.com/Collektive/collektive/compare/0.1.0...0.2.0) (2023-09-13)


### Features

* added align function used by the compiler plugin to modify the stack ([586990b](https://github.com/Collektive/collektive/commit/586990b10fd2c8f377811ac7915dffc4fcde8c45))
* added dsl main functions ([d3cd4ff](https://github.com/Collektive/collektive/commit/d3cd4ff702c1ce4405225f0d515ba28fe68fc99a))
* added field data structure ([5baddf2](https://github.com/Collektive/collektive/commit/5baddf2ffa16335bc5a6e3837a66cddc4e8f1e63))
* **aggregate:** added first implementation of repeating function ([c164d38](https://github.com/Collektive/collektive/commit/c164d386ae87346e9b792e50de672b8ff50f9cad))
* **aggregate:** added function with different signature to run multiple time ([748d588](https://github.com/Collektive/collektive/commit/748d5882cf7bcc958039484a879dc4feb8888c1a))
* **aggregate:** added repeating implementation ([7a6a34b](https://github.com/Collektive/collektive/commit/7a6a34be3737268a9261c9efc8bb4bf816f4bd54))
* **aggregate:** added stack instance ([24c6d48](https://github.com/Collektive/collektive/commit/24c6d48c1cd854b6ff7256114920c2fdc1c5bf6f))
* **aggregate:** added the possibility to run multiple times ([a755065](https://github.com/Collektive/collektive/commit/a7550652c53250f5eaeec41055c8ea4c79c6b1cb))
* **aggregate:** added usage of messages ([33c3d21](https://github.com/Collektive/collektive/commit/33c3d219abc7db50f0927ab67128a41145aa051b))
* **aggregate:** aggregate can receive a network instance ([2ed2b46](https://github.com/Collektive/collektive/commit/2ed2b469fde1041ba791713cf406dc87efe12f50))
* **aggregate:** changed generics type to allow all the function as argument ([cc90ea9](https://github.com/Collektive/collektive/commit/cc90ea94e6f24fb78d6711c28d6ee4b27dbfff45))
* **aggregate:** created computational options in a new file ([74a7e74](https://github.com/Collektive/collektive/commit/74a7e74b003aa3f657efe749568efad19bebaf3b))
* **aggregate:** implemented neighbouring function ([7bf7cbe](https://github.com/Collektive/collektive/commit/7bf7cbea65c402284f9226dcd10fa3af9201f5ee))
* **aggregate:** implemented new version of neighbouring ([b341030](https://github.com/Collektive/collektive/commit/b34103056603305f6787da5f0be2b353a12393db))
* **aggregate:** implemented repeating with new data structures ([0436d5a](https://github.com/Collektive/collektive/commit/0436d5a09dd90b41abd7c30de6ee06c1568c54b6))
* **aggregate:** implemented sharing with new data structure ([4269872](https://github.com/Collektive/collektive/commit/42698727035527bf1207aa22c7e3279d0dbf8a43))
* **aggregate:** modified aggregate to use ID and network classes ([1775d67](https://github.com/Collektive/collektive/commit/1775d674a0b790a088fb1c366c4a124b11085466))
* **aggregate:** neighbouring throws an exception if the field requested is not present ([349694e](https://github.com/Collektive/collektive/commit/349694e9ed07a5f35ab00eafc5641ece032e5373))
* **aggregate:** neighbouring uses generics instead of any ([d925d16](https://github.com/Collektive/collektive/commit/d925d169154eb82c3c2925390258d4d40166f551))
* **aggregate:** neighbouring uses the new field implementation ([bbb159c](https://github.com/Collektive/collektive/commit/bbb159c50d64049825f7397f28935e6d4eafb36d))
* **aggregate:** overloaded aggregate function to be able to create easily a multiple running program ([f83cfcd](https://github.com/Collektive/collektive/commit/f83cfcd2832503dbbb4bc99c0872b1799efe1410))
* **aggregate:** run until can be created with a network instance to use for communication ([9598bf4](https://github.com/Collektive/collektive/commit/9598bf416421e84bf11ccebeabdb9ec86ef78991))
* **aggregate:** the messages are handled by switching the indexes only if necessary ([d600c7a](https://github.com/Collektive/collektive/commit/d600c7ac7e8a9672bf4b94baf5405703c786e895))
* **aggregate:** updated dsl function to use the stack object and removed stack handling ([4df1596](https://github.com/Collektive/collektive/commit/4df1596c8ef96e4d48ef41999f45dc09d5c7f170))
* **collektive-test:** added distance function to device ([222da9b](https://github.com/Collektive/collektive/commit/222da9b30ee692a2c37dd4963832411db5751ef8))
* **collektive-test:** added distance sensor device ([0c0b567](https://github.com/Collektive/collektive/commit/0c0b5671e89ef17228e651542dc46ee4b90b3681))
* **collektive-test:** added distance sensor to collektive device ([d12bf03](https://github.com/Collektive/collektive/commit/d12bf0348d25d418008708caa1f6809f0e321f8e))
* **collektive-test:** added effect file ([beddd4f](https://github.com/Collektive/collektive/commit/beddd4f76911e7e6ab5229b4013b6dc0ec6a0209))
* **collektive-test:** added gradient algorithm ([e9afb45](https://github.com/Collektive/collektive/commit/e9afb4583428111d2bd17b9d382121362efeb42c))
* **collektive-test:** added gradient extension function to aggregate ([3211aa1](https://github.com/Collektive/collektive/commit/3211aa147b36fe5969fc32afcb83ddaa8466901e))
* **collektive-test:** added node to constructor of the aggregate entry point to access the node ([125bff2](https://github.com/Collektive/collektive/commit/125bff21c2bbdfcb95f56550225de824e11a977e))
* **collektive-test:** changed class names ([9936013](https://github.com/Collektive/collektive/commit/9936013de19b49b3e54b2b897c9143671837288c))
* **collektive-test:** changed effect for gradient ([6723250](https://github.com/Collektive/collektive/commit/6723250d230f66dafdb28b295a514b937b444506))
* **collektive-test:** changed number of nodes and steps ([4dbd02e](https://github.com/Collektive/collektive/commit/4dbd02eee3429cb07040e54595b8c12414c061d6))
* **collektive-test:** created collektive-test module and used succesfully dsl in main ([7db8753](https://github.com/Collektive/collektive/commit/7db87534dfd17c2b5cbbda3e44b1c973343accf3))
* **collektive-test:** fixed gradient example ([8a2fd0b](https://github.com/Collektive/collektive/commit/8a2fd0bfd2ece9ed19d740690c54b8528fad0111))
* **collektive-test:** functioning simulation with Alchemist GUI ([b1bb64a](https://github.com/Collektive/collektive/commit/b1bb64a2a6c59bf9754311182659aa9716df4a0c))
* **collektive-test:** modified number of nodes generated and the range of their generation ([87c7914](https://github.com/Collektive/collektive/commit/87c7914dfb984ab4eb66d32823bd7bf6f8f0b6c6))
* **collektive-test:** refactored execute function ([ced34e9](https://github.com/Collektive/collektive/commit/ced34e9e0999f0e9ee147acf7497beed21ad7ca6))
* **compiler-plugin:** added aggregate context class name as constant ([9471c59](https://github.com/Collektive/collektive/commit/9471c597a40247fc269d5ac929550320304e823f))
* **compiler-plugin:** added aggregate function name ([4de761e](https://github.com/Collektive/collektive/commit/4de761ecd9664249a321bb8ba111d372c8b5156c))
* **compiler-plugin:** added alignment to aggregateContext's extension functions ([4cacfb2](https://github.com/Collektive/collektive/commit/4cacfb2c4b5c1edee294a182ca0d653fdff5dc17))
* **compiler-plugin:** added all the utility functions in the same file ([a3c1e37](https://github.com/Collektive/collektive/commit/a3c1e37ccb01fd8c221300932a79eff10990ac7d))
* **compiler-plugin:** added extension function to look for a matching type in the irCall's receiver and arguments ([ef2bbda](https://github.com/Collektive/collektive/commit/ef2bbda007785e1a0373d6c19b91f2d004f0fdcd))
* **compiler-plugin:** added extension function used to get the last value argument of a certain function call ([d8a93f1](https://github.com/Collektive/collektive/commit/d8a93f15092ecf720da12aa0d660fdc2cb988eec))
* **compiler-plugin:** added file for all the branch extension functions ([e46576e](https://github.com/Collektive/collektive/commit/e46576eb07a0b6dc0896d6937e512528e1fc3935))
* **compiler-plugin:** added file for all the branch extension functions ([05fd2cc](https://github.com/Collektive/collektive/commit/05fd2cc4eedd678c453e914494021d8ee2d0756a))
* **compiler-plugin:** added handler for more return types ([9d382ac](https://github.com/Collektive/collektive/commit/9d382aca9b7f4f0162d65907f7fc325be5819706))
* **compiler-plugin:** added handling of more than one condition in the if statement ([9e83b26](https://github.com/Collektive/collektive/commit/9e83b264318a2309edebdff0967dd490277d6e4a))
* **compiler-plugin:** added handling of when, which is basically an if else block ([cdbf957](https://github.com/Collektive/collektive/commit/cdbf957eaa3f53be2728efb984c4e44e69b8791c))
* **compiler-plugin:** added if handling, with all the condition type and the block as body ([aa79942](https://github.com/Collektive/collektive/commit/aa79942ec5175c2f08d9d57790a236c2a561da47))
* **compiler-plugin:** added the command line option to enable the compiler plugin ([c706a9b](https://github.com/Collektive/collektive/commit/c706a9b4b876c08aca2a1ba418c8a3f19d094fc2))
* **compiler-plugin:** added the handling of the else branch ([bb03a11](https://github.com/Collektive/collektive/commit/bb03a11d7b9db5c87eab6060ab5b8a41c9254d6f))
* **compiler-plugin:** added the research to the alignment function ([03c8307](https://github.com/Collektive/collektive/commit/03c830794669d126a2f3b5c571e0397151eaf85e))
* **compiler-plugin:** added trasformer that activet the research of all the function called inside of aggregate ([3fd0499](https://github.com/Collektive/collektive/commit/3fd04995a93a227866b47960064597f8751330aa))
* **compiler-plugin:** added visitor that collects all the function call inside of aggregate ([6581156](https://github.com/Collektive/collektive/commit/6581156d8d318e3e3c90c09f2ea8c59c18cf3f53))
* **compiler-plugin:** changed name of the function that handle the alignment ([90ba5a0](https://github.com/Collektive/collektive/commit/90ba5a0384cc83c9b3270f650fd43c9d136308f3))
* **compiler-plugin:** changed the return type of the aggregate context class retrieval ([149ad88](https://github.com/Collektive/collektive/commit/149ad88197efb1dbdd620ea7c8cef68a9ca0da2a))
* **compiler-plugin:** created alignment function call and added function declaration to not modify ([82fc0d8](https://github.com/Collektive/collektive/commit/82fc0d801dd68a85dc0112ef10b3c24de2c1f434))
* **compiler-plugin:** created enum that contains all the element' names needed ([8116a77](https://github.com/Collektive/collektive/commit/8116a779e9f9abda276ccae72191a5799b530d1e))
* **compiler-plugin:** created handler when the result of the branch is not a block but an expression ([6b6399d](https://github.com/Collektive/collektive/commit/6b6399d347ffd490fa90a369e5b9e89b615fb91b))
* **compiler-plugin:** created just one function to create a irStatement that will contains the alignOn call ([efbce3d](https://github.com/Collektive/collektive/commit/efbce3d387258878ffcacea80bd16695c9b4041f))
* **compiler-plugin:** created trasformer that modify the function declaration starting from function call ([0196dec](https://github.com/Collektive/collektive/commit/0196decad93fc72894cfca3f3bd847b77c34cd01))
* **compiler-plugin:** created visitor that retrieve the aggregate context class ([66eb4ff](https://github.com/Collektive/collektive/commit/66eb4ff6a8ae7a12994ea5408f44e77f85df60e5))
* **compiler-plugin:** created visitor used to search for the alignment function ([41141b5](https://github.com/Collektive/collektive/commit/41141b5f4cc37ec633dfa7de4fa91b9cf3b943b1))
* **compiler-plugin:** fixed alignment when a function call has a lambda body ([ade36f9](https://github.com/Collektive/collektive/commit/ade36f9fcc0c2de424fad88af6967eccf07fdadb))
* **compiler-plugin:** implemented extension called during compilation to call the trasformer ([6963a05](https://github.com/Collektive/collektive/commit/6963a0503f15db9f023f43625d139da99dcbeb53))
* **compiler-plugin:** modified the function used to retrieve the alignedOn function ([eb26757](https://github.com/Collektive/collektive/commit/eb26757660734217398defe2c22f9e2c84914978))
* **compiler-plugin:** modified the generation extention in order to retrieve alse the aggregate context class ([5aa795f](https://github.com/Collektive/collektive/commit/5aa795fca076941712267e0b28a700e94d5b1b64))
* **compiler-plugin:** registered the ir generation extension component ([99bacde](https://github.com/Collektive/collektive/commit/99bacdea835784f651a965c725f66feeaab988e8))
* **compiler-plugin:** removed branch extension functions from transformer ([eedd883](https://github.com/Collektive/collektive/commit/eedd8837f0b17768992f0d9ae08f6fe6954be633))
* **compiler-plugin:** removed the unused visitors ([cba9a15](https://github.com/Collektive/collektive/commit/cba9a158b25336314f216190760453be91d102e0))
* **compiler-plugin:** the alignedOn function and the AggregateContext class are found without needing to compile the dsl ([8add0a1](https://github.com/Collektive/collektive/commit/8add0a138e2aa83fdcf77593cc8cebfece0aa0b9))
* **compiler-plugin:** the statement creation is in a separate file now ([d0d3d74](https://github.com/Collektive/collektive/commit/d0d3d74dc4da190dfdf6593b0e0c6c3ae52cdf39))
* **compiler-plugin:** the visitor for the aggregate context reference returns a irExpression? instead of a list of expression ([816feda](https://github.com/Collektive/collektive/commit/816feda867136aa8bbd90a2eafbe2436328265a9))
* **compiler-plugin:** trasform every function adding a print of the name as first statement ([d370301](https://github.com/Collektive/collektive/commit/d370301d0ab1bcf5add65e423d3b92c0bd38b61f))
* **computation:** at the beginning of each computational cycle the stack is cleared ([d4d12a4](https://github.com/Collektive/collektive/commit/d4d12a4ca341db1268d67988d405a4dd4e0c090d))
* **computation:** the stack is cleared at the beginning and at the end of the computational cycle ([758beec](https://github.com/Collektive/collektive/commit/758beec229a323eaaebdaf0a8981a9f9a51195d3))
* created a main to briefly check how to use the functions ([8e1778b](https://github.com/Collektive/collektive/commit/8e1778b8373d31580bc33e0e76c79d626c3d0e0e))
* **dsl:** added documentation to aggregate functions ([f6e93fb](https://github.com/Collektive/collektive/commit/f6e93fbadcc064a3034b760f0e9387f3a438fe75))
* **dsl:** added main with extension function in order to verify it works ([8fb481d](https://github.com/Collektive/collektive/commit/8fb481dbd079979c94950ddf56e8b43a4b99644a))
* **dsl:** added optional parameters to aggregate ([ed912a0](https://github.com/Collektive/collektive/commit/ed912a08dd23857f8ecd2908a265438473e893db))
* **dsl:** fixed aggregate function layout ([d7aae31](https://github.com/Collektive/collektive/commit/d7aae31f1dc9d6d1c39c53d66afa2df7d18c7920))
* **dsl:** fixed main ([552c118](https://github.com/Collektive/collektive/commit/552c118be736f8bb632482d947c61b9bc11dde9a))
* **dsl:** fixed sharing Behavior ([d7191ec](https://github.com/Collektive/collektive/commit/d7191ec4f45dae7c24c12f8988c88dbcba4a531f))
* **dsl:** remove useless comments ([14e5d3b](https://github.com/Collektive/collektive/commit/14e5d3b356389dc681bba4ae531d1052080f1a36))
* **dsl:** removed default parameters ([3b1a685](https://github.com/Collektive/collektive/commit/3b1a68597487eec1b118b93f75aaafb12fcf3e8b))
* **dsl:** removed main ([7b44a0b](https://github.com/Collektive/collektive/commit/7b44a0b8a41c24948b015b0fd9f5e5be2cd2b0f0))
* **environment:** added global fields instance ([a42bdc0](https://github.com/Collektive/collektive/commit/a42bdc055a4eb61a9ffe4b16a33653ba7b13b6e8))
* **environment:** added id for test purposes ([78d5b29](https://github.com/Collektive/collektive/commit/78d5b2917ba5dcefad9370ea0592d96d38884b86))
* **environment:** incapsulated fields in environment ([4262d03](https://github.com/Collektive/collektive/commit/4262d0365edd6e822855458e973785e0814df395))
* **event:** added event class and interface ([8d6df0f](https://github.com/Collektive/collektive/commit/8d6df0fe0ed490e8f676906e324e7bf32ee604be))
* **event:** added event interface and class ([f873118](https://github.com/Collektive/collektive/commit/f873118e421a9d683b4713d6ad9e9a451e217c8d))
* **event:** createdd strategy to calculate the event identifier by hashcode ([4a60f36](https://github.com/Collektive/collektive/commit/4a60f368ca68562862b613fba705a2f21ea0ee06))
* **field:** added function to add a new entry in field ([ac3d3cb](https://github.com/Collektive/collektive/commit/ac3d3cbbdb1538e784d76b13495c519e9a7b2e21))
* **field:** added function to get a value by id ([519b0fa](https://github.com/Collektive/collektive/commit/519b0fadbd73d1ec5fbeb59e797f2bc976701e29))
* **field:** added function to map a field ([6ee5482](https://github.com/Collektive/collektive/commit/6ee5482327d4929665c656d5662b2273767f487f))
* **field:** added function to retrieve the max value from a field ([f0b7f97](https://github.com/Collektive/collektive/commit/f0b7f97ab5350031372b65eefe3000291bead5da))
* **field:** added function used to retrieve the min in a field ([fa3459d](https://github.com/Collektive/collektive/commit/fa3459decd8de9fe1d008ec82f676c01a8122661))
* **field:** added method to get the size of the field ([c7d7d17](https://github.com/Collektive/collektive/commit/c7d7d17f9c25f111ea5080237bbd9ea3938ae204))
* **field:** added neighbour messages to field as property ([42e44f0](https://github.com/Collektive/collektive/commit/42e44f01558cc1b1f9fe5b80db61cd066acb622a))
* **field:** added plus operator ([cdc9b02](https://github.com/Collektive/collektive/commit/cdc9b02254d14ff8a5d0da1301021df626ce38f1))
* **field:** added plus operator ([4e29122](https://github.com/Collektive/collektive/commit/4e29122bc3b081b5d22fb13f18d7b271ce972324))
* **field:** added to string method ([173b7c0](https://github.com/Collektive/collektive/commit/173b7c0afabb672ea661f3342d5f84acd79c79a7))
* **field:** changed field constructor ([93d5590](https://github.com/Collektive/collektive/commit/93d55904286ef08da2d743e55e3e0ca81e6dc729))
* **field:** changed implementation of min, max and plus ([6ccff92](https://github.com/Collektive/collektive/commit/6ccff92a07e3fbc4aac98d3d63b610893e0aa58c))
* **field:** changed interface and used it instead of its implementation ([d16a258](https://github.com/Collektive/collektive/commit/d16a2584071db5e90548e868c5b6fa87ed90f841))
* **field:** get by id throws an exception if the key does not exist ([7a123f8](https://github.com/Collektive/collektive/commit/7a123f8ff5dcd5f14de519783f64a112381b53fe))
* **field:** modified field constructor and private map ([6737ffb](https://github.com/Collektive/collektive/commit/6737ffb5fa9b9568edeb6d082e740ebf43f4e86a))
* **field:** moved field into package and removed id generic ([625b0e9](https://github.com/Collektive/collektive/commit/625b0e9cf885ebb21e60a026f4f4a47f9864888c))
* **field:** removed field plus operation from interface ([c057f5d](https://github.com/Collektive/collektive/commit/c057f5dcca7b1cf81e540ea419bca73154e4b7d7))
* **fields:** added data class for local fields ([db94cf2](https://github.com/Collektive/collektive/commit/db94cf2ec0c25f3abc604ef0244a9a371f2afc0a))
* **fields:** added data structure to hold and create a field ([d5de17b](https://github.com/Collektive/collektive/commit/d5de17be5a3e2c22f241f48aeebad1a04df2d1f5))
* **fields:** added global fields as data class ([8f172a0](https://github.com/Collektive/collektive/commit/8f172a09104dde7b6d8d5b8d8e57151fd5f5e168))
* **fields:** added method to check if a certain field exists ([db73c5b](https://github.com/Collektive/collektive/commit/db73c5b374409a8d84aed3e939fcd514881fee2d))
* **fields:** added retrieve field by type method ([487e38d](https://github.com/Collektive/collektive/commit/487e38d9ebdd1331c3bdd0a75e3e9b011bfec629))
* **fields:** changed fields into a abstract class ([44d3944](https://github.com/Collektive/collektive/commit/44d394455bd593be06dd314370a4fb266eb337b8))
* **fields:** changed map key in the hashCode of the event ([535b491](https://github.com/Collektive/collektive/commit/535b4914de2f1ec7c46b921a97a1cc3b09e32cbb))
* **field:** the generic type is not bounded to Any ([87c5cf6](https://github.com/Collektive/collektive/commit/87c5cf6840eaa1936610a4bb7cfa986ce7da24c8))
* **gradle-plugin:** added custom gradle property to enable or disable the plugin ([cb6428a](https://github.com/Collektive/collektive/commit/cb6428a91ae00bb726033ea60ea6546045bc5f7e))
* **gradle-plugin:** created connection to the actual compiler plugin ([8c3d04b](https://github.com/Collektive/collektive/commit/8c3d04bb7e1b939c902c55d3f85b13bf40d0d534))
* **id:** changed id type in int ([d244f7b](https://github.com/Collektive/collektive/commit/d244f7bbf349d87ce296c6aaeb06bb3f6e08c412))
* **id:** created interface and data class for the device id ([63b8bb8](https://github.com/Collektive/collektive/commit/63b8bb8820ada5237050898faf1e79748b7764c6))
* **identifier:** create strategy to create an event identifier from stack trace ([606ae48](https://github.com/Collektive/collektive/commit/606ae487e1a2022b9d090e700c62711fa3f7bd74))
* **main:** added call to neighbouring function ([6410f7f](https://github.com/Collektive/collektive/commit/6410f7f5a52a734b2f7544b3d29ac492c5f935b7))
* **main:** calling repeating more time to check the final result ([85527da](https://github.com/Collektive/collektive/commit/85527daa266b9d6c13303aa9c9e0e8e44108d095))
* **main:** created basic main to easily test the dsl ([36ace3a](https://github.com/Collektive/collektive/commit/36ace3a44adc24237ee30c439283634accdaf35a))
* **main:** created code in main to check how repeating looks ([f3fe6d5](https://github.com/Collektive/collektive/commit/f3fe6d53608c64e86c454901c38b0c6ed8ba8391))
* **main:** created example to run multiple devices multiple times ([9e5b913](https://github.com/Collektive/collektive/commit/9e5b913e46f8e7ba5231e92f1133908a38c70d0a))
* **main:** created main to check the possible result ([a2543df](https://github.com/Collektive/collektive/commit/a2543df1ee7657717cd5979b1c02dc443c8e6f28))
* **main:** main uses the min operator in the field ([79511a9](https://github.com/Collektive/collektive/commit/79511a95fdc4ec1a38f32524ac655df0e44684b5))
* merged working dsl to master ([86f9d8d](https://github.com/Collektive/collektive/commit/86f9d8d70af715adba843e88726d8cf3e7b00036))
* **network:** created network used to send and receive messages ([163983b](https://github.com/Collektive/collektive/commit/163983bb1dea2809e0f8f06888743c528e7ea96f))
* **network:** removed local id from the network constructor and added in the send method ([4b9bae4](https://github.com/Collektive/collektive/commit/4b9bae48159354a7f657b4d39b1aff7b4d578e41))
* **path:** created path data structure ([5ead81a](https://github.com/Collektive/collektive/commit/5ead81ab2362c37176f4024d9a784f7e16653781))
* **path:** removed function to remove a token from the path ([fa531fb](https://github.com/Collektive/collektive/commit/fa531fb1d80f1c4e9301e21cb91d7cb75698a742))
* **plugin:** added functions that have to be ignored by the plugin ([68d5fc8](https://github.com/Collektive/collektive/commit/68d5fc81936db382867aafb9c5aebfa467cf2afd))
* removed linter ([342e8da](https://github.com/Collektive/collektive/commit/342e8daf1269d5d5cb3d62a2733ae563f2959550))
* **stack:** created stack implementation, which also handles the current path updates ([421640c](https://github.com/Collektive/collektive/commit/421640cc3ff8a5cf3db5cf0e6b1a5c850305a78d))
* **stack:** created stack object and path ([2abf063](https://github.com/Collektive/collektive/commit/2abf063019305ff4f5493ad05bd6ec51754f05d1))
* **stack:** removed print of path when opening a new frame ([cbb3abf](https://github.com/Collektive/collektive/commit/cbb3abf4b85776779f1ef96a6fa9f95915be8f0d))
* **test:** added dsl test to check if the expression in a lambda are aligned ([f9ffc95](https://github.com/Collektive/collektive/commit/f9ffc9533eb9c52b3b6cf126cd34986a6aa38c02))
* **test:** added stack tests ([10b0dee](https://github.com/Collektive/collektive/commit/10b0deebab07b41bd5cc8602d5cadb881f4d474f))
* **test:** fixed field manipulation test ([b414215](https://github.com/Collektive/collektive/commit/b41421507a0b84d5b8f493e91ea00f31189e7480))
* **token:** created enum for the known token at compile time ([6ca3b8d](https://github.com/Collektive/collektive/commit/6ca3b8d1801332c7a88628e19537cd60b82d858e))
* updated readme ([995cdc4](https://github.com/Collektive/collektive/commit/995cdc4a152cf6824ccc030558fd90b2279f112f))
* **util:** added switch indexes operator to map data structure ([e3df3b1](https://github.com/Collektive/collektive/commit/e3df3b169ad6af373993fc291026441e14a1c3d6))


### Dependency updates

* **core-deps:** update kotlin monorepo to v1.9.10 ([219f4ea](https://github.com/Collektive/collektive/commit/219f4eadc7b0b575cfab12c7326d1fefcd124305))
* **deps:** cleanup dependencies and updated ([0e33b70](https://github.com/Collektive/collektive/commit/0e33b7066e050e17f6b03f62b13f0b6fbea6b9f1))
* **deps:** update dependency com.github.gmazzo.buildconfig to v4 ([0cb3847](https://github.com/Collektive/collektive/commit/0cb3847b17f151e851d3e3f219d84460e8c62911))
* **deps:** update dependency gradle to v8.3 ([c203ab0](https://github.com/Collektive/collektive/commit/c203ab06f813c6c91aef4859e31b62b70461cc8a))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.43 ([0e1f21b](https://github.com/Collektive/collektive/commit/0e1f21bc67fe53fb1f31df4539269bf214c64bdf))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.44 ([4a931c0](https://github.com/Collektive/collektive/commit/4a931c01c0abbb684fa484d51b0e9a71f01e65e4))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.46 ([ed536fc](https://github.com/Collektive/collektive/commit/ed536fcebbb32d151ef5b9df02751d3847b5da26))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.47 ([b2e5af9](https://github.com/Collektive/collektive/commit/b2e5af953e50011c882aa33e088d4d46ff54fa84))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.48 ([aab5881](https://github.com/Collektive/collektive/commit/aab5881b5b6bc90609157441ed6c01e5dc5fae07))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.49 ([14f49c5](https://github.com/Collektive/collektive/commit/14f49c54a6785e28ac9de6c1c9d662c6454908d4))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.50 ([ebd0aa1](https://github.com/Collektive/collektive/commit/ebd0aa1ec024031d09f245ef9130717bbba90648))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.51 ([5ded159](https://github.com/Collektive/collektive/commit/5ded159639bb0e6851f3b24aa123131482cdd9c6))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.52 ([6e68db9](https://github.com/Collektive/collektive/commit/6e68db93a8738c8d0bb4977b346fd4d22ec6693d))
* **deps:** update dependency semantic-release-preconfigured-conventional-commits to v1.1.53 ([072f86d](https://github.com/Collektive/collektive/commit/072f86d3994690b99acc300800c6f4100a11d0f2))
* **deps:** update kotest to v5.7.0 ([8ed1594](https://github.com/Collektive/collektive/commit/8ed15945581b7e2f02ae5cf5afb2ba4e1675d15f))
* **deps:** update kotest to v5.7.1 ([0916dfa](https://github.com/Collektive/collektive/commit/0916dfa103765a0abd66d68989b1e0628bd5381a))
* **deps:** update kotest to v5.7.2 ([7174e3d](https://github.com/Collektive/collektive/commit/7174e3d625a619d005750c4f8bf5fc272415ad5c))
* **deps:** update node.js to 16.20 ([400dd7f](https://github.com/Collektive/collektive/commit/400dd7f2eba058806268120a2da0ee0bbc3ad686))
* **deps:** update node.js to v18 ([ae23b98](https://github.com/Collektive/collektive/commit/ae23b980ae8b4a78ae02ebfeb3536b894765db3e))
* **deps:** update plugin com.gradle.enterprise to v3.14.1 ([bb937c4](https://github.com/Collektive/collektive/commit/bb937c49008b645bfb70d0c6304db2c15c933389))
* **deps:** update plugin multijvmtesting to v0.4.23 ([8c99759](https://github.com/Collektive/collektive/commit/8c9975958e11980abb8dc2a91f654ef6758647b0))
* **deps:** update plugin multijvmtesting to v0.5.4 ([157fa19](https://github.com/Collektive/collektive/commit/157fa19701923c43c0dd0bcf5b383b96d4b5afcc))
* **deps:** update plugin multijvmtesting to v0.5.5 ([86c5f20](https://github.com/Collektive/collektive/commit/86c5f209930fd7fff73d4f88dea5f2c50f79a9f7))
* **deps:** update plugin org.danilopianini.gradle-pre-commit-git-hooks to v1.1.10 ([410a409](https://github.com/Collektive/collektive/commit/410a409a10c1cede1991d0d1869d782db8f6bf5e))
* **deps:** update plugin org.gradle.toolchains.foojay-resolver-convention to v0.7.0 ([adb4215](https://github.com/Collektive/collektive/commit/adb4215c2ec4b11f739253e228ece2144e3a72ca))
* **deps:** update plugin publishoncentral to v5.0.11 ([d0e4531](https://github.com/Collektive/collektive/commit/d0e45310773e3b8f6c2c864079e8d8494afdf40f))
* **deps:** update plugin publishoncentral to v5.0.12 ([fe3b288](https://github.com/Collektive/collektive/commit/fe3b2886681aab0108c91adcc16daf478caf8f00))
* **deps:** update plugin publishoncentral to v5.0.13 ([39d8fff](https://github.com/Collektive/collektive/commit/39d8fff0e3ffd59c959cff7c51fea6b14c4de6fe))
* **deps:** update plugin tasktree to v2.1.1 ([747eda1](https://github.com/Collektive/collektive/commit/747eda1bf90b0e7f21ac56cc15deb4216037b545))


### Bug Fixes

* adapted to usage of local fields data class ([148290c](https://github.com/Collektive/collektive/commit/148290c29b1d9b6705ee8b8372efefb4f331c014))
* **aggregate-context:** adapted aggregate context to the new version of stack ([508a932](https://github.com/Collektive/collektive/commit/508a9324726e0f147e8f7befdba6fd6d1f844d17))
* **aggregate:** adapted code to use event calss ([798d9f4](https://github.com/Collektive/collektive/commit/798d9f4e588e9b4e8487e0774933b5488dc07257))
* **aggregate:** added usage of event ([6268498](https://github.com/Collektive/collektive/commit/62684982777d597512a4ac9a63bd7cee5d22f978))
* **aggregate:** removed ID from field ([f0b0a21](https://github.com/Collektive/collektive/commit/f0b0a21eb287ba3271520e6f70e85ce9fc836ee5))
* **aggregate:** removed id initialization in single cycle call ([b13f05d](https://github.com/Collektive/collektive/commit/b13f05d6ad1fb90bb5e65d6b04e3dd8e48d1a166))
* **aggregate:** removed inline and reified from neighbouring ([62aca69](https://github.com/Collektive/collektive/commit/62aca69787796bc1f9cc8b166dcb5252740a636d))
* **all:** general work idea ([7e664e1](https://github.com/Collektive/collektive/commit/7e664e178e51b2c91a6e1173b491b9865dd6451d))
* **collektive-test:** moved file into correct folders ([dfaaf0b](https://github.com/Collektive/collektive/commit/dfaaf0bab90b62909fd7df523c6c60f1f8983c30))
* **collektive-test:** reduced line characters ([d61319d](https://github.com/Collektive/collektive/commit/d61319d827e78042c999b858fcea222ba431352d))
* **collektive-test:** removed message to self ([45b9a3d](https://github.com/Collektive/collektive/commit/45b9a3d9ae14042c7cc5d3a1475227c8643daba0))
* **collektive-test:** removed println ([ec7b804](https://github.com/Collektive/collektive/commit/ec7b804367ae7ebd3485e19e262453458734c5d8))
* **compiler-plugin:** added non empty alignment functions collection check ([2d2770f](https://github.com/Collektive/collektive/commit/2d2770f3cd5bb13848306c40fbf1db5d75b9cf56))
* **compiler-plugin:** changed parameters from SimpleFunction to Function ([b066afc](https://github.com/Collektive/collektive/commit/b066afc242d70a0e66b3f00e8be013a2534ecd4c))
* **compiler-plugin:** fixed problem when examining the children of a function, which has to be done only if that function does not have a reference to aggregate context ([3b36d9c](https://github.com/Collektive/collektive/commit/3b36d9c03b22a35f10c9b5f1951eaec9cc232899))
* **compiler-plugin:** removed the visitor of the aggregate call body because it was unnecessary ([65cf0b3](https://github.com/Collektive/collektive/commit/65cf0b35d795b2833aab942f1057c921a6490ad7))
* **compiler-plugin:** the enum for elements' names is now an object ([bb9fc15](https://github.com/Collektive/collektive/commit/bb9fc15dbe4119751013b066a7c457a907fb9a05))
* **compiler-plugin:** the lambda body of aggregate is the last argument ([24e546e](https://github.com/Collektive/collektive/commit/24e546ea43c6d925b197dbabf9ffcd11b34f0514))
* **compiler-plugin:** the search of the aggregate reference must be different if there is a irCall ([237d7b2](https://github.com/Collektive/collektive/commit/237d7b26f6325eb256bd4ddd6a7b5004d5ccd6e0))
* **compiler-plugin:** the tranformer is applied to the aggregate body directly, without visiting the children ([58305ac](https://github.com/Collektive/collektive/commit/58305ac33e2bfcf24b45f11b49319315c80765c6))
* **computation:** added default value in local id of single cycle ([af69650](https://github.com/Collektive/collektive/commit/af69650c966aa2a43eb35baeb39cf07b03e4670d))
* **computation:** removed the function to empty the stack after every computation of aggregate ([3a671f8](https://github.com/Collektive/collektive/commit/3a671f83ad3afe51d14bb091aa6a6bdaf22e0917))
* **dsl:** removed java 8 requirment ([965205d](https://github.com/Collektive/collektive/commit/965205d2bfeb50c0a7cef738fe587dc7c1f9f610))
* **dsl:** removed unused paramenter to id's interface ([85481e1](https://github.com/Collektive/collektive/commit/85481e19a877d0fc688e3ab67b34ca0ff3f60dd3))
* **field:** changed map from immutable to mutable ([ae19be8](https://github.com/Collektive/collektive/commit/ae19be8db470c32d583b5efb55a6cd38d5571803))
* **field:** fixed generic type in field ([55ace74](https://github.com/Collektive/collektive/commit/55ace7477747df02952a280597e6d6881f2c5a42))
* **field:** removed field size from the functions ([fa6ef51](https://github.com/Collektive/collektive/commit/fa6ef5132a0de359cde775b67370151147b88309))
* **fields:** added code to use event class ([fdd1672](https://github.com/Collektive/collektive/commit/fdd1672d94766bb240889a7eb9a4ff81e5e11294))
* **fields:** added the usage of event ([af7f6e2](https://github.com/Collektive/collektive/commit/af7f6e24faed780cc81a3d5c4f6c88b86cd59e77))
* **fields:** changed fields in a class and used a mutable map istead of a list ([3d4d0e4](https://github.com/Collektive/collektive/commit/3d4d0e41e47ee85bc9f70d3047d70b87e64b076b))
* **fields:** retrieve field does not return a nullable field ([8d85e9a](https://github.com/Collektive/collektive/commit/8d85e9a932e0c6421b9df2f5018c99c17bee1ed5))
* **fields:** the retrieval of the key from the map can return a nullable element ([af5f331](https://github.com/Collektive/collektive/commit/af5f3313bbbf2f93ba685beab102f88a85055762))
* **identifier:** used stack trace as return value in jvm ([7d680e2](https://github.com/Collektive/collektive/commit/7d680e24e6011c980be4e27a1ea0e584ad536af5))
* removed unsupported editor config ([d5b597f](https://github.com/Collektive/collektive/commit/d5b597fdc4b9dce4a00d55a2040a56ca64b3bfb8))
* **stack:** changed stack from object to class ([6ed2a07](https://github.com/Collektive/collektive/commit/6ed2a0796e496a2a67a7edc24f691991755989f0))
* **stack:** removed stack prints every time the stack is modified ([316e53b](https://github.com/Collektive/collektive/commit/316e53b6025f028923ce77f6f02c61d4ee0756ee))
* **test:** fixed tests to conform to the new field constructor ([e3310fe](https://github.com/Collektive/collektive/commit/e3310fe97c19164fb821cc578b29df69ad6c6e50))
* **test:** remove id from field ([f6fa2eb](https://github.com/Collektive/collektive/commit/f6fa2eb935b4048adb069d55cc42586496e8e39b))
* use error instead of throwing excpetion ([5032120](https://github.com/Collektive/collektive/commit/503212010a7125badace9d52dcbc5f4b973d1bf1))


### Documentation

* **deps:** update dependency org.jetbrains.dokka to v1.9.0 ([7ff51bb](https://github.com/Collektive/collektive/commit/7ff51bb94c43fab8ba3a36429324e7da460ded91))


### Tests

* added check empty field in common test ([eaf19f5](https://github.com/Collektive/collektive/commit/eaf19f540f5e697a5ac34df413c9fc089c460809))
* **aggregate:** added test for neighbouring ([2da93d7](https://github.com/Collektive/collektive/commit/2da93d76987bfbc8e7be6807bc142383ea828e76))
* **aggregate:** added test for neighbouring successful and failing ([8c6b37a](https://github.com/Collektive/collektive/commit/8c6b37abf329ac4273383477433eb70104194169))
* **aggregate:** added tests for sharing ([80b8ccc](https://github.com/Collektive/collektive/commit/80b8ccc34b89eb309248aa5093b1ac0695028b0e))
* **branch:** added tests to verify the correct Behavior of if, else and when blocks ([e08d113](https://github.com/Collektive/collektive/commit/e08d1135bd858e8b3837309684dcbec2ce5be97d))
* **branch:** fixed test mistakes in branches tests ([79d57f7](https://github.com/Collektive/collektive/commit/79d57f73ddbd0678e0016da3b58fa6e1d003ddf9))
* **field:** adapted get by id test to catch the exception ([937b70c](https://github.com/Collektive/collektive/commit/937b70c7f07adc522d5269759ec958cbab3711b0))
* **field:** added test for field with and without messages from other connected devices ([44a89a8](https://github.com/Collektive/collektive/commit/44a89a8d38fed633e726e6d8b86499ae10704520))
* **field:** added test when trying to retrieve a field by an id that does not exist ([faa305f](https://github.com/Collektive/collektive/commit/faa305fa2ca740e640867f70a71ef017806c7dbd))
* **field:** added tests for field manipulation ([097c344](https://github.com/Collektive/collektive/commit/097c344db72ab1106ac6feb3b9f624983298a62e))
* **field:** fixed usage of IntId for id generation ([a381d98](https://github.com/Collektive/collektive/commit/a381d98855cb946cc713acff104422321ea727ac))
* **field:** moved field tests in its package ([d240b9a](https://github.com/Collektive/collektive/commit/d240b9adcf0634513c3330946c40b600290bb2f5))
* **field:** moved field tests in its package ([8c7c30e](https://github.com/Collektive/collektive/commit/8c7c30e3281d71ebf77fb85b91c262a64e27f4dc))
* **fields:** added modify field test ([923db96](https://github.com/Collektive/collektive/commit/923db96fec5862e76e1537c9e726598c93847734))
* **fields:** added usage of event ([3aad6fb](https://github.com/Collektive/collektive/commit/3aad6fb4ff190c603052f40b6e019fb3e1209cae))
* **fields:** changed test to match the new fields class ([f05a7b8](https://github.com/Collektive/collektive/commit/f05a7b8d016bd09a21bbf7703f468b280091ece1))
* **fields:** created add field test ([9fd94d2](https://github.com/Collektive/collektive/commit/9fd94d2ca080253dfeeb38a36eb66c3daa9a6b36))
* **fields:** fixed test of adding an element to the field ([281c453](https://github.com/Collektive/collektive/commit/281c453307752e1ecde96a93e56f8c1b16440120))
* **fields:** removed println in modify field test ([4b1e12c](https://github.com/Collektive/collektive/commit/4b1e12c33737effe2f8119d81cfe977a62122605))
* **field:** test adding entry to field ([dc1d5d0](https://github.com/Collektive/collektive/commit/dc1d5d01b62aa14baae188fbebdf9e14617ebe25))
* **field:** test get by id ([d6b9b9d](https://github.com/Collektive/collektive/commit/d6b9b9d87d4350aa378933d453953813d25b410c))
* **neighbouring:** added test for neighbouring operator with no external messages ([23cc9d2](https://github.com/Collektive/collektive/commit/23cc9d25249862c51f0b807b6fbc5ddb2b299fd9))
* **neighbouring:** added usage of event ([85ffbe8](https://github.com/Collektive/collektive/commit/85ffbe8b22e08c9f301b601e447cef5abcbade8e))
* **neighbouring:** created test for the new version of neighbouring ([114f5a3](https://github.com/Collektive/collektive/commit/114f5a34d019adaf2c76e927ffdfdc954e01ac8d))
* **neighbouring:** fixed the argument from Event to simple function ([dda0a30](https://github.com/Collektive/collektive/commit/dda0a30971d17c4fabac1af630bf97376ed4d328))
* **path:** added test for path class ([62568b5](https://github.com/Collektive/collektive/commit/62568b51f7b767d56634b315871863438ae60a71))
* **path:** removed path tests ([66a863c](https://github.com/Collektive/collektive/commit/66a863cdf6000e25d3959732c365027c70c63ce8))
* removed test of old classes and data structures ([e634e17](https://github.com/Collektive/collektive/commit/e634e17ba144c815fca83a73e7fa37132a98500d))
* **repeating:** added test for repeating function ([acd8b40](https://github.com/Collektive/collektive/commit/acd8b405c11a4d8519806574a4c120914cc679df))
* **repeating:** added test for the usage of repeating once and more than once times ([64ab58b](https://github.com/Collektive/collektive/commit/64ab58bae49ec257a4f231a219e30f78c18cad09))
* **repeating:** added usage of event ([06be2a4](https://github.com/Collektive/collektive/commit/06be2a4117599db10e3f10c2e90b67261037c0e1))
* **stack:** commented stack's test ([4febf2a](https://github.com/Collektive/collektive/commit/4febf2a5633236936f7836173c2439d284a15155))
* **stack:** created test to verify the stack Behavior ([9d7ad78](https://github.com/Collektive/collektive/commit/9d7ad788f90240d540ff5bdc978c2f917bfcfc2f))
* **stack:** fixed stack tests ([a9673a2](https://github.com/Collektive/collektive/commit/a9673a26243291b2e9d6ba56074c5de0c5cdc335))


### Build and continuous integration

* added test dependencies ([a54061d](https://github.com/Collektive/collektive/commit/a54061d3b24ebe75dd6896eab34e90e4a3039452))
* **ci:** added test execution on multiple os ([50e78b1](https://github.com/Collektive/collektive/commit/50e78b178f99a0f5f55aac99704194ddc4195911))
* **ci:** added test run on push ([19014a3](https://github.com/Collektive/collektive/commit/19014a3526066366c82d35cb571fd6c501b58289))
* **ci:** granted execution permission to gradlew ([4752793](https://github.com/Collektive/collektive/commit/4752793139dd958b4f0726c231b954656297ef7f))
* **collektive-test:** added required alchemist dependencies ([5dd79fc](https://github.com/Collektive/collektive/commit/5dd79fc696c79a803e36ddc37a70b30ef6b11809))
* **collektive-test:** fixed alchemist dependencies ([b90f432](https://github.com/Collektive/collektive/commit/b90f432cbdf83bb35cd7568c2262c4430574fb07))
* **collektive-test:** fixed jupiter dependencies ([6b1979e](https://github.com/Collektive/collektive/commit/6b1979e063db1984655abd26bfc5ad7cd96a0588))
* **collektive:** added compiler plugin in library ([6683a93](https://github.com/Collektive/collektive/commit/6683a93e455660030eae255e65017d20392dd8bc))
* **collektive:** added linter ([e8e0125](https://github.com/Collektive/collektive/commit/e8e0125de7e9d99ea9b7c9ce2a686a5407a282d9))
* **collektive:** fixed library catalog ([b616a4d](https://github.com/Collektive/collektive/commit/b616a4d08aeee313578a97234ca7e7fceea6c81a))
* **collektive:** include collektive-test ([33a88a3](https://github.com/Collektive/collektive/commit/33a88a3461d521e9c258473071c9b072e4507311))
* **compiler-plugin:** added gradle build in compiler plugin subproject ([420361e](https://github.com/Collektive/collektive/commit/420361ef5b7332a53e6d2d96d25a843d296d7912))
* **compiler-plugin:** removed unused dependencies ([586e471](https://github.com/Collektive/collektive/commit/586e471c977e9b81ffbc64854aced5bed4d747ee))
* **deps:** update actions/checkout action to v3 ([bcfd700](https://github.com/Collektive/collektive/commit/bcfd700e0d3136c2693a01b900bb3c95ba602c7e))
* **deps:** update actions/checkout action to v4 ([d621041](https://github.com/Collektive/collektive/commit/d621041c7b75453199761725d71f488a72af4709))
* **deps:** update actions/setup-node action to v3.8.1 ([50cc0d4](https://github.com/Collektive/collektive/commit/50cc0d4ba83a897b3f2ea4b64faa446857015ff7))
* **dsl:** added dependency ([1905480](https://github.com/Collektive/collektive/commit/190548089fe04ca34dac9215ece67dab5bbe7a60))
* **dsl:** added gradle setup for dsl project ([c693c3d](https://github.com/Collektive/collektive/commit/c693c3da8c226e0a420e7d7eeec92e3271d65663))
* **dsl:** added task to run all tests ([6d444de](https://github.com/Collektive/collektive/commit/6d444de31a9965494deb18ad2446e4da6a4fc959))
* **dsl:** changed compiler plugin dependency ([6bb748c](https://github.com/Collektive/collektive/commit/6bb748c11b5a76648e902fb1dd283c1dd86c75f3))
* **dsl:** changed name of the plugin used for the compiler plugin ([df46862](https://github.com/Collektive/collektive/commit/df468627d19e388c75b5107903e088fe2fd1a613))
* **dsl:** cleaned build.gradle ([c5a4477](https://github.com/Collektive/collektive/commit/c5a447707026863a70ae5db2801aa7e992cd3f2a))
* **dsl:** modified jvm target ([1248be9](https://github.com/Collektive/collektive/commit/1248be9addefb387cd4d12d190ba0b14c3191682))
* **dsl:** modified jvm target ([2c0c478](https://github.com/Collektive/collektive/commit/2c0c4781f363152e33f521aecbd418b92a08290d))
* **dsl:** the tests must dependo on the common code ([d22cef9](https://github.com/Collektive/collektive/commit/d22cef94c55887d1cc861acee0c8d6e086f338a0))
* **gradle-plugin:** created gradle plugin to expose ([10d04d4](https://github.com/Collektive/collektive/commit/10d04d4e749af8ce83a51350e738e6c6199b84a2))
* included projects and added dependecies ([1b80ef9](https://github.com/Collektive/collektive/commit/1b80ef90a9c3122c7cefffcf5ec1ed63a013cb95))
* organized dependencies ([2101a96](https://github.com/Collektive/collektive/commit/2101a96ceae90656692b9c3f083495c2fdee4666))
* remove uneeded file ([8a0c27b](https://github.com/Collektive/collektive/commit/8a0c27bd7b6734121e1c95529001691616ffb908))
* removed comments in setting gradle ([68efff6](https://github.com/Collektive/collektive/commit/68efff6167ac15f956c94a758e1a8d46a966ed32))
* removed custom task to run all the tests ([6cd0fb6](https://github.com/Collektive/collektive/commit/6cd0fb6930a1f80de18c1d9389b4133aeb89475f))
* reorganized gradle's build ([8f5e2f0](https://github.com/Collektive/collektive/commit/8f5e2f06301199e20f0464280bed55d842ff3844))
* set gradle plugin portal credentials ([38c1410](https://github.com/Collektive/collektive/commit/38c141074dbeedb5857b4e7068e87316c55f911e))
* setup ci workflow for publish the library ([6a649b2](https://github.com/Collektive/collektive/commit/6a649b2ab1872738e25821f257d98359e2f49754))


### General maintenance

* add mergify config ([81b5ceb](https://github.com/Collektive/collektive/commit/81b5cebab3b3e199271cc5e9d23ddd0933aa5b1c))
* **aggregate:** added align on documentation ([a527f78](https://github.com/Collektive/collektive/commit/a527f78aad43048a8c72cedd381ec8a146fa48d2))
* **build:** minor gradle refactoring ([b696c2a](https://github.com/Collektive/collektive/commit/b696c2a9bd7845f1b01a78be1cd9a1d27933e995))
* **build:** setup pubblication for also the included build (compiler plugin and gradle plugin) ([5e2d8cf](https://github.com/Collektive/collektive/commit/5e2d8cfdb53899e04ab95fd42b27097f46d7165c))
* **build:** upload plugin project only from Linux runner ([c08dcee](https://github.com/Collektive/collektive/commit/c08dceee5580f62758014bb708e60fa777b91252))
* **build:** use a single catalog source for all submodules ([f59e4f6](https://github.com/Collektive/collektive/commit/f59e4f601e6f28f63e7ed732be0ee2ba56cedcd3))
* **build:** use compiler plugin as local project via classpath ([4de3a0c](https://github.com/Collektive/collektive/commit/4de3a0ce972cc03e1705565bbd5c84a8fc0219bf))
* changed project name ([3d6f0fb](https://github.com/Collektive/collektive/commit/3d6f0fb117e0f3f614c0d4e5e3319854b631226a))
* **compiler-plugin:** added correct documentation ([0f97b23](https://github.com/Collektive/collektive/commit/0f97b238520bbb635675139f3d4723a22c5a1353))
* **compiler-plugin:** added documentation ([28efed8](https://github.com/Collektive/collektive/commit/28efed8792a0372acdd055c7a1d6a0c8f4b74f9d))
* **compiler-plugin:** added the usage of the extension function getLastValueArgument ([156de58](https://github.com/Collektive/collektive/commit/156de584d3d19a2395903b91eb5707c4e4ecd6ad))
* **compiler-plugin:** added the usege of all the utility functions ([bd40fd3](https://github.com/Collektive/collektive/commit/bd40fd32408f0e749b447d2504715fc64b2baa68))
* **compiler-plugin:** added usage of extension function for irCall's matching type of receiver and arguments ([0277403](https://github.com/Collektive/collektive/commit/0277403df5f9373d3dedc15ce1937bb1e331eeb9))
* **compiler-plugin:** fixed grammar error in documentation of alignment component registrar ([af5bb00](https://github.com/Collektive/collektive/commit/af5bb00ed7b19fc05c2f734d7c305a8a81da8251))
* **compiler-plugin:** fixed the package and the import of the statement and call utils ([41dc124](https://github.com/Collektive/collektive/commit/41dc1247c73e5723df684fed0f3f2cc26a9a404c))
* **compiler-plugin:** modified the arguments order ([993a68c](https://github.com/Collektive/collektive/commit/993a68c6ca5ae5454cd3f54aef5f493c274cbd39))
* **compiler-plugin:** moved in correct packege call and statements utils ([6d867ca](https://github.com/Collektive/collektive/commit/6d867cae81dc603d9923e05682f9a155391e5c61))
* **compiler-plugin:** moved the argument order for the alingOnCall builder ([f4f1e75](https://github.com/Collektive/collektive/commit/f4f1e75edb02234fe02b9bd2e9a07ac68c67233b))
* **compiler-plugin:** moved the name object in the utils\common folder ([1304404](https://github.com/Collektive/collektive/commit/13044041888df960ff5c8dbcb9b3deb1c425c76f))
* **compiler-plugin:** moved utility functions in file that underlines the usages ([b07270d](https://github.com/Collektive/collektive/commit/b07270dfb8585ed719c08920808d50689e02e90a))
* **compiler-plugin:** moved utility functions in file that underlines the usages ([39b70d0](https://github.com/Collektive/collektive/commit/39b70d0054148850adb26ed2b037a23f4daede1a))
* **compiler-plugin:** refactor of the compiler plugin lambda creation ([c293750](https://github.com/Collektive/collektive/commit/c293750a7533b8a4210285a2f3f5c3087c2bea3f))
* **compiler-plugin:** removed lambda utility classes from transformer ([05e7251](https://github.com/Collektive/collektive/commit/05e7251e933bad32e0afe16407f3f3e196808f35))
* **compiler-plugin:** removed unused import ([20eb770](https://github.com/Collektive/collektive/commit/20eb7709f16633c6825d4537162cb2efbd0ad788))
* **compiler-plugin:** rename of arguments ([259831b](https://github.com/Collektive/collektive/commit/259831b54825647f24622ce4bc42a57ca8952789))
* **compiler-plugin:** renamed aggregate ir element transformer ([b1cc6de](https://github.com/Collektive/collektive/commit/b1cc6def7888534e295b66b783c5992cee58c325))
* **compiler-plugin:** renamed and moved aggregate reference visitor ([7872ea7](https://github.com/Collektive/collektive/commit/7872ea729599179486746de90d4a16dcfd599eb0))
* **compiler-plugin:** renamed arguments to make them more expressive ([b4af587](https://github.com/Collektive/collektive/commit/b4af587b006db38ec809546c9ae408a63613fb32))
* **compiler-plugin:** renamed the class AggregateIrElementTransformer in AggregateCallTransformer ([d1e5f2b](https://github.com/Collektive/collektive/commit/d1e5f2b0e0270e18b2f7c400ebde880c6b7db459))
* **compiler-plugin:** renamed the visitor that looks for the alignedOn function ([3cdd5a0](https://github.com/Collektive/collektive/commit/3cdd5a0626f1fe676a46b092bd2fcd0991d7d19d))
* **computation:** removed stack import ([6fcf1ea](https://github.com/Collektive/collektive/commit/6fcf1ea977e252421e96098c74c42b54bb38c17c))
* configure common metadata and refactoring ([b88b312](https://github.com/Collektive/collektive/commit/b88b3122f5d49c3e0e6dc7aebf038e62ce5cb32f))
* convert into a real multiplatform project ([d3be9af](https://github.com/Collektive/collektive/commit/d3be9af8f4df7a603ea5081b6fc7a4cc2073206a))
* deleted platform object ([e5e0186](https://github.com/Collektive/collektive/commit/e5e01860d9006eeac2a3e93408bea1ec603e937d))
* disable collektive-test project. It should be moved on a separate repository ([53446de](https://github.com/Collektive/collektive/commit/53446de09277444a9f377ac6709c8771f5c19f3b))
* **dsl:** moved dsl in a new gradle project ([15822b2](https://github.com/Collektive/collektive/commit/15822b2e14f8bda54dd8e5dd4a31750b45b93294))
* exclude node_modules ([bd6ff41](https://github.com/Collektive/collektive/commit/bd6ff4121c6de3c6d51df98f3b195787f6949975))
* **fields:** removed print ([256667c](https://github.com/Collektive/collektive/commit/256667c9a3f335ef95338e24486845e08b041243))
* **gradle-plugin:** added documentation to gradle extension ([68d818b](https://github.com/Collektive/collektive/commit/68d818b55e0cf9d855f990d3455dfe9e37932fa0))
* **gradle-plugin:** changed name of the gradle plugin with a more expressive one ([7c7ae59](https://github.com/Collektive/collektive/commit/7c7ae592dd7ff1cd7112d9f7d27db0303bf06880))
* ignore diabled target warning ([70d1efa](https://github.com/Collektive/collektive/commit/70d1efa3a6d7e0ee7fc53dc5111cc0a619a3f95d))
* moved neighbouring test in the aggregate test folder ([ff9f863](https://github.com/Collektive/collektive/commit/ff9f863cd76cd80b99fd4ff7914747cdd9a7c316))
* remove no needed suppress warning ([928ccdd](https://github.com/Collektive/collektive/commit/928ccdd3e5717cbbd18de8b4f60e901bcdf3dde7))
* remove withJava ([8439f71](https://github.com/Collektive/collektive/commit/8439f71d529bb34c5962b866153ce4f8b06f7ba8))
* removed environment ([c5f466d](https://github.com/Collektive/collektive/commit/c5f466d9cd589cc19dc0563ab93c589de4b369c8))
* removed local properties file ([b3bba5a](https://github.com/Collektive/collektive/commit/b3bba5ab9e1600df83a4a260c32ce4c4baaf9b7f))
* removed local.properties ([dc33ff7](https://github.com/Collektive/collektive/commit/dc33ff7088437f03b5bac304e9396c858f67b0fb))
* removed not used libraries ([52e40f2](https://github.com/Collektive/collektive/commit/52e40f234414096f0965159fd525b4243afbc9fb))
* renamed project ([89b8723](https://github.com/Collektive/collektive/commit/89b872305a546304ad8a0ea7adf8c0a8fdc2b8d2))
* **renovate:** add renovate configuration ([18bd739](https://github.com/Collektive/collektive/commit/18bd7396d50f2a3ba11e8bc834517666a620848c))
* revert to kotlin 1.9.0 ([3b43ba5](https://github.com/Collektive/collektive/commit/3b43ba53eca3c28a6d8b818f5be23ea7f75f631b))
* setup config for semantic-release ([f130859](https://github.com/Collektive/collektive/commit/f130859f747b50c998b9642fa5543f7f2597d9e4))
* **stack:** modified import of stack classes in aggregate ([50d5df0](https://github.com/Collektive/collektive/commit/50d5df06a7a7230eb8bcd8dcff00fa17c76d42e9))
* **stack:** moved stack related classes in the stack package ([f855624](https://github.com/Collektive/collektive/commit/f8556248f5cff94f6fd46123538b0f88f05a1c77))
* suppress problematic tasks ([9243236](https://github.com/Collektive/collektive/commit/9243236a9b983d258cb20e76ec22cc3ea7008ec6))
* the main has nested function to verify the alignment ([64514a5](https://github.com/Collektive/collektive/commit/64514a5304b53d86ceceb8b5205d8294d4255942))
* there is hope here ([c537c06](https://github.com/Collektive/collektive/commit/c537c06e8b59006f1b49b50df30721615a47a7b8))
* update renovate config ([5ac66c2](https://github.com/Collektive/collektive/commit/5ac66c26091d003acd3b7ee9e1b36ea88c945a85))
* updated readme ([81b1b43](https://github.com/Collektive/collektive/commit/81b1b43601ce8e1b53d01cd6a14a5b697e23a326))
* updated readme ([bed4c70](https://github.com/Collektive/collektive/commit/bed4c7046d5b947dd81738688d90c839e07b13ea))
* updated test default folder ([ba25249](https://github.com/Collektive/collektive/commit/ba2524992146f5e1b0f0cdfa2e2cb6bc7237ac7c))
* use up-to-date compiler plugin API and fix bug usage ([2350a1e](https://github.com/Collektive/collektive/commit/2350a1e22d624daaee22d7466829bfae4fd87a06))
* written main that can actually use the new implementation of the dsl operators ([354cdfc](https://github.com/Collektive/collektive/commit/354cdfcf2409c7f8a3e2fa83bdb3ae3f6a03c997))


### Style improvements

* reformat code ([11b762e](https://github.com/Collektive/collektive/commit/11b762ece53d5337bd181f44878c53a41cd2890c))
* reformat code ([90bc1fd](https://github.com/Collektive/collektive/commit/90bc1fdc3d554b3eb7173cd9c4db0636f3bbe565))


### Refactoring

* abstract stack into an interface providing a default implementation based on ArrayDequeue ([41c86ab](https://github.com/Collektive/collektive/commit/41c86ab243ddf95db39d97a5f83c7edcdefa46a1))
* move into private function raw cast suppression and implement rep, nbr and share into a more kotlin idiomatic fashion ([74c28b6](https://github.com/Collektive/collektive/commit/74c28b6a5533887ed6f4f0a6c5b48c61e628fd69))
* remove kapt ([0e681da](https://github.com/Collektive/collektive/commit/0e681dacecc1bfa91b3af048729c6c1a07f37911))
* rename package in it.unibo.collektive ([5b3dc6b](https://github.com/Collektive/collektive/commit/5b3dc6b8b78b9b4cf9f8a298acaaddf01d7477bc))
* rewrite field implementation to avoid raw cast ([6669c0c](https://github.com/Collektive/collektive/commit/6669c0cedaa7faed8390486b0fc8501fa5a69322))
