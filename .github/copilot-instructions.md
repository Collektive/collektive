# Collektive - Kotlin Multiplatform Aggregate Programming

Collektive is a Kotlin multiplatform implementation of Aggregate Programming that builds core field calculus mechanisms directly into the Kotlin compiler via compiler plugins.

**ALWAYS follow these instructions first** and only fallback to additional search and context gathering if the information here is incomplete or found to be in error.

## Working Effectively

### Prerequisites and Setup
- **Java**: Requires Java 17 or higher (project uses OpenJDK 17.0.16)
- **Node.js**: Site documentation requires Node.js 22.19 (version 20.19.4 works but shows warnings)
- **Gradle**: Uses Gradle 9.0.0 via wrapper (./gradlew) - downloads automatically

### Core Build Commands
**CRITICAL**: Set timeouts of 60+ minutes for build commands. NEVER CANCEL long-running builds.

#### Full Multiplatform Build (Complete validation)
- **Full build and check**: `./gradlew build check --no-daemon` -- takes ~30 minutes. NEVER CANCEL. Set timeout to 60+ minutes.
  - Compiles all targets: JVM, JavaScript, Native (Linux x64/ARM64, Windows, macOS)
  - Runs all tests across all platforms
  - Performs all code quality checks
  - Use for final validation before commits

#### JVM Development (Recommended for fast iteration)
- **Compile JVM targets**: `./gradlew :collektive-dsl:compileKotlinJvm :collektive-stdlib:compileKotlinJvm :alchemist-incarnation-collektive:compileKotlin --no-daemon` -- takes ~30 seconds. NEVER CANCEL.
- **Run JVM tests**: `./gradlew :collektive-dsl:jvmTest :collektive-stdlib:jvmTest :alchemist-incarnation-collektive:test --no-daemon` -- takes ~1 minute. NEVER CANCEL. Set timeout to 15+ minutes.
- **Quick validation**: `./gradlew :collektive-dsl:compileKotlinJvm :collektive-dsl:jvmTest --no-daemon` -- takes ~45 seconds for core DSL module

#### Code Quality and Linting
- **Run all linting**: `./gradlew ktlintCheck detektAll --no-daemon` -- takes ~30 seconds. NEVER CANCEL. Set timeout to 10+ minutes.
- **Format code**: `./gradlew ktlintFormat --no-daemon`

#### Documentation Site
- **Install dependencies**: `cd site && npm install` -- takes ~50 seconds. Set timeout to 15+ minutes.
- **Build site**: `cd site && npm run build` -- takes ~30 seconds. Expect SVG warnings (normal). Set timeout to 10+ minutes.
- **Serve locally**: `cd site && npm run serve` (for testing changes)


### Build Timing Expectations
- **NEVER CANCEL**: All builds and tests MUST complete. Set generous timeouts.
- Full multiplatform build: 25-35 minutes
- JVM compilation: 30 seconds - 2 minutes
- JVM tests: 1-3 minutes  
- Linting: 30 seconds - 2 minutes
- Site build: 1-2 minutes
- Full dependency download (first time): 5-10 minutes

## Validation

### Always Validate Changes With These Steps
**For fast iteration (JVM only)**:
1. **Compile JVM targets**: `./gradlew :collektive-dsl:compileKotlinJvm :collektive-stdlib:compileKotlinJvm --no-daemon`
2. **Run core tests**: `./gradlew :collektive-dsl:jvmTest :collektive-stdlib:jvmTest --no-daemon`
3. **Check code quality**: `./gradlew ktlintCheck detektAll --no-daemon`

**For complete validation (before final commit)**:
1. **Full multiplatform build and test**: `./gradlew build check --no-daemon` (~30 minutes)
2. **Build documentation**: `cd site && npm run build`

### Manual Testing Scenarios
After making changes to core DSL or stdlib:
- Create a simple test program using the DSL to verify it compiles
- Test that the compiler plugin still catches alignment errors correctly
- Verify that examples in README.md still work

### CI/CD Validation
The CI pipeline runs comprehensive tests including:
- Multi-platform builds (Windows, macOS, Linux)
- All target platforms (JVM, Native, JS)
- Full test suite with coverage reports
- Code quality checks

**Always run the validation steps above before committing** to catch issues early.

## Repository Structure

### Key Modules
- **collektive-dsl**: Core domain-specific language for aggregate programming
- **collektive-stdlib**: Standard library with common aggregate programming patterns
- **collektive-compiler-plugin**: Kotlin compiler plugin that enables field calculus
- **collektive-gradle-plugin**: Gradle plugin for easy project setup
- **alchemist-incarnation-collektive**: Integration with Alchemist simulator
- **collektive-test-tooling**: Testing utilities and helpers
- **site/**: Docusaurus documentation website

### Important Files
- **build.gradle.kts**: Root build configuration with all subprojects
- **settings.gradle.kts**: Gradle settings with included builds
- **.github/workflows/**: CI/CD pipeline definitions
- **gradle.properties**: Gradle configuration and JVM settings

## Common Tasks

### Adding New DSL Features
1. Modify **collektive-dsl** module
2. Add corresponding tests in **collektive-dsl/src/commonTest/**
3. Update **collektive-stdlib** if new standard functions needed
4. Run: `./gradlew :collektive-dsl:jvmTest :collektive-stdlib:jvmTest --no-daemon`
5. Always run linting: `./gradlew ktlintCheck detektAll --no-daemon`

### Working on Compiler Plugin
1. Changes go in **collektive-compiler-plugin**
2. Test with **collektive-compiler-plugin-test** module
3. Run: `./gradlew :collektive-compiler-plugin-test:test --no-daemon`
4. Validate against **collektive-dsl** tests: `./gradlew :collektive-dsl:jvmTest --no-daemon`

### Documentation Updates
1. Edit files in **site/docs/** or **site/blog/**
2. Test locally: `cd site && npm run start`
3. Build for production: `cd site && npm run build`
4. Validate all links and examples work

### Before Committing
**For fast iteration** (during development):
```bash
# Compile and test core modules (JVM only)
./gradlew :collektive-dsl:jvmTest :collektive-stdlib:jvmTest :alchemist-incarnation-collektive:test --no-daemon

# Check code quality  
./gradlew ktlintCheck detektAll --no-daemon
```

**For final validation** (before committing):
```bash
# Full multiplatform build and test (~30 minutes)
./gradlew build check --no-daemon

# Build documentation
cd site && npm run build && cd ..
```

Set timeouts of 60+ minutes for the full sequence. NEVER CANCEL running builds or tests.

## Troubleshooting

### Network-Related Build Failures (Historical)
Previously, native target compilation failed due to download.jetbrains.com access restrictions, but these have been resolved. If you encounter network issues:
- **Current status**: Full multiplatform builds work correctly
- **Fallback option**: Use JVM-specific tasks for faster iteration: `:collektive-dsl:jvmTest` instead of just `test`
- **Build scan publishing**: May occasionally fail but does not affect build success

### Node.js Version Warnings
- Site build shows engine warnings for Node.js version mismatch
- **Current**: Node.js 20.19.4, **Required**: 22.19
- **Impact**: Site builds successfully despite warnings
- **Solution**: Warnings are safe to ignore; functionality works correctly

### Gradle Daemon Issues
- Always use `--no-daemon` flag for reproducible builds
- Daemon may cause memory issues in constrained environments
- **Solution**: Consistently use `--no-daemon` in all commands

### Test Failures
- For fast iteration during development, use JVM-specific test tasks: `:moduleName:jvmTest`
- For complete validation, use the full multiplatform build: `./gradlew build check --no-daemon`
- All platform tests (JVM, JavaScript, Native) are now working correctly