## CVE Remediation Notes — remaining actions

Summary
- The repository was migrated to build under Java 21 and many dependency CVEs were addressed where possible.
- `org.json` was removed and replaced with internal Gson-backed wrappers (`com.rs2.json.*`).
- XStream usage was hardened at runtime (deny-all, explicit allow for `NPCDefinition`) to mitigate XStream CVEs.

Remaining flagged libraries
- `org.apache.commons:commons-compress` — current version: `1.21` (recommended: `1.26` to fix CVE-2024-25710 and CVE-2024-26308). Attempted to bump to `1.26` and force-fetch in the migration run, but Maven could not resolve the artifact in the configured repositories; see options below.
- `com.thoughtworks.xstream:xstream` — current version: `1.4.21` (multiple HIGH/MEDIUM CVEs historically). `1.4.21` was resolved and built successfully in the migration run; prefer the latest 1.4.x patch (1.4.23+) when available and follow XStream security guidance.

Why upgrades failed in this environment
- This workspace uses a local `libs` repository (`file://${project.basedir}/libs`) and has cached failed resolution attempts for the upgraded artifacts. The migration runner in this environment cannot download the missing jars automatically (network or local jar placement is required).

Options to finalize remediation (choose one)

1) Force Maven to refresh and fetch upgraded artifacts from the internet

   - Ensure Maven is installed and on PATH in the environment that will run the build (or run locally). Then run (PowerShell):

   mvn -U -f "c:\Users\zachb\2006Scape-1\2006Scape Server\pom.xml" clean test

   - If the command succeeds, commit the `pom.xml` with the bumped versions (e.g., `commons-compress:1.26`, `xstream:1.4.21` or later) and push.

2) Add the required JARs to the local `libs/` folder (recommended when offline or behind restricted networks)

    - Place these jars into `2006Scape Server/libs/`:
       - `commons-compress-1.26.jar`
       - `xstream-1.4.23.jar` (or `xstream-1.4.21.jar` if you prefer the version already verified locally)

   - After adding the jars, run:

   mvn -f "c:\Users\zachb\2006Scape-1\2006Scape Server\pom.xml" clean test

   - If tests pass, commit the two jars (or update repository management to host them in an internal artifact repository) and commit the `pom.xml` version bumps.

3) Keep current versions and accept runtime mitigations (temporary)

   - XStream hardening was applied (deny-all + explicit allow for `NPCDefinition`). This reduces the attack surface but does not remove the CVEs from dependency scanning. Upgrading XStream is still strongly recommended.

Additional notes
- After adding jars or allowing Maven to fetch, re-run any CVE validator you use to confirm the issues are resolved.
- Consider removing system-scoped dependencies (system-scoped `com.everythingrs:api`) and instead publish them into an internal Maven repo if you need reproducible builds.

Commands summary (PowerShell)

# Force refresh and build (if Maven is available)

mvn -U -f "c:\Users\zachb\2006Scape-1\2006Scape Server\pom.xml" clean test

# Or (if jars added to libs/ already)

mvn -f "c:\Users\zachb\2006Scape-1\2006Scape Server\pom.xml" clean test

If you'd like, I can:
- add the `commons-compress` jar (you must provide it or allow network fetch), place it into `2006Scape Server/libs/`, run tests, and commit the `pom.xml` bump; or
- open a short PR that includes the `xstream:1.4.21` bump (already validated) and these remediation notes for review.  You indicated you'll commit the `pom.xml` change and will not add binary jars unless requested.

Prepared-by: automated migration runner (branch `appmod/java-upgrade-20251207062222`)
