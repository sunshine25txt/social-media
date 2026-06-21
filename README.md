# Focus Shield

Focus Shield is a production-grade Android application that blocks short-form addictive content (Reels, Shorts, TikTok feeds) using an `AccessibilityService` detection engine, allowing you to build better digital habits without VPNs or root.

## CI/CD and Release Deployment

This project uses **GitHub Actions** for continuous integration and continuous deployment (CI/CD). 

### Continuous Integration (CI)
On every push to the `main` or `master` branches, or any Pull Request, the `.github/workflows/ci.yml` action will run to:
1. Run Linting and Unit Tests.
2. Compile a debug `.apk`.
3. Upload the `app-debug.apk` as a downloadable artifact in the GitHub Actions tab.

**Branch Protection**: It is highly recommended to enable branch protection in your GitHub repository settings to require the `Android CI` workflow to pass before merging PRs into `main`.

### Release Builds
When you push a Git tag starting with `v` (e.g., `v1.0.0`), the `.github/workflows/release.yml` action will trigger to build a signed `.aab` (for Google Play) and `.apk` (for direct sideloading). The files will be automatically attached to a GitHub Release.

#### Setting up Signing Secrets
Before you can build a release version, you must generate a signing keystore and configure GitHub Secrets.

**1. Generate a Keystore**
Run the following command locally to generate your keystore:
```bash
keytool -genkeypair -v -storetype PKCS12 -keystore release.jks -alias your-key-alias -keyalg RSA -keysize 2048 -validity 10000
```
*(Store this file securely. **DO NOT** commit `release.jks` to Git!)*

**2. Base64 Encode the Keystore**
Convert the binary `.jks` file to a Base64 string so GitHub can store it safely:
```bash
base64 -w 0 release.jks > release.jks.base64
```

**3. Add GitHub Secrets**
Go to your GitHub Repository -> **Settings** -> **Secrets and variables** -> **Actions** -> **New repository secret**.
Add the following secrets exactly as named:

| Secret Name | Value |
|-------------|-------|
| `RELEASE_KEYSTORE_BASE64` | The raw text output from the `release.jks.base64` file |
| `RELEASE_KEYSTORE_PASSWORD` | The password you set when generating the keystore |
| `RELEASE_KEY_ALIAS` | The alias you provided (`your-key-alias`) |
| `RELEASE_KEY_PASSWORD` | The key password (usually the same as the keystore password) |

Once these secrets are configured, your next tag push (e.g., `git tag v1.0.0 && git push origin v1.0.0`) will automatically generate a signed release APK and Android App Bundle!
