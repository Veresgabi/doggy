# Copilot instructions for this repository

Scope:
- Applies to Java source files under src/main/java
- Primary target: suggestions inside repository implementation classes (classes named *RepositoryCustomImpl, especially `DogRepositoryCustomImpl`)

Naming convention override (Hungarian-style for this project scope):
- Prefer Hungarian-style local variable prefixes inside repository implementation classes to make intent explicit in short scope.
- Conventions used in this repo for repository impls (examples):
    - String -> strName, strLifeSpan, strQuery
    - Integer/Long -> iPage, iMaxResults, lCount
    - List<T> -> lstDogs, lstPredicates
    - Predicate -> predFilter, predForName
    - Path -> pathBreedGroup
    - CriteriaBuilder -> cb or criteriaBuilder
    - EntityManager -> em
    - Root<T> -> rootDog

Examples (before -> preferred suggestion):
- name -> strName
- lifeSpan -> strLifeSpan
- predicatesForBreedGroups -> lstPredicatesForBreedGroups
- filterPredicate -> predFilter
- dog -> rootDog

Other guidance:
- Keep method and class names unchanged unless refactoring is requested; this rule only affects local variable naming in suggestions for repository implementation classes.
- Keep suggestions minimal and consistent with imports and existing types.

## How to Apply These Instructions

### For Documentation/Reference (this file)
This file serves as the **project-level documentation** of coding conventions and GitHub Copilot guidelines for your team. Store it in `.github/instructions/` for version control.

### For JetBrains IDE Users (to activate Copilot suggestions)
**Note:** JetBrains GitHub Copilot plugin does NOT automatically read `.github/instructions/` files. Instead, it uses local user configuration.

To apply these instructions in JetBrains IDEs you have two convenient options on Windows:

Option A — Manual copy

1. **Locate your local GitHub Copilot config directory:**
    - Windows: `C:\Users\<YourUsername>\AppData\Local\github-copilot\intellij\`
    - macOS: `~/.config/github-copilot/intellij/`
    - Linux: `~/.config/github-copilot/intellij/`

2. **Copy the instructions:**
    - Create or update the file `copilot-instructions.md` in that directory and paste the contents of this file.

3. **Restart your IDE:**
    - Restart JetBrains IDE or reload the GitHub Copilot plugin to pick up the changes.

Option B — Run the provided installer script (recommended on Windows)

We include a small PowerShell helper script that copies this repository's instructions into your local Copilot configuration directory and makes a timestamped backup of any existing file.

From the repository root run (PowerShell):

    ./.github/scripts/install-copilot-instructions.ps1

This script will copy `.github/instructions/copilot-instructions.md` into your local Copilot config directory (under `%LOCALAPPDATA%\github-copilot\intellij`) and print the destination path. After running it, restart your JetBrains IDE or reload the plugin.

4. **Verify it's active:**
    - You should see suggestions that follow the Hungarian-style naming conventions (e.g., `strName`, `lstDogs`, `predFilter`).

### If you prefer a different prefix style
Edit the conventions in both this file and your local GitHub Copilot config (for example, short one-letter prefixes: sName, iPage, or camelCase alternatives).
