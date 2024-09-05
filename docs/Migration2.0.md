## Migrating from 2.0 to 3.0

A LOT of breaking changes happened between v2 and v3, the most important one being that GCAPI is now **independent of StationAPI** (cause StationAPI now uses GCAPI itself), and proper config validation on load.

Most breaking changes are package changes, and annotation changes, with only one major change happening elsewhere for proper validation on load.

- **Entrypoints and the mod id is now** `gcapi3`
- `@ConfigEntry` and `@ConfigCategory` now house all information about the field is on, removing the need for checking for 6 different annotations on load.
- `@GConfig` had it's `primary` value removed, and instead replaced with `priority`.
- All packages have changed to allow for co-existence with GCAPI2.
- Config verification has been moved from clientside classes, and is now applied whenever a config value is loaded.
- Added a new optional field to `ConfigEntry`: `textValidator`. This will be used by default on any custom config types, unless `isValid` is overridden.
- Array types now require `getTypedArray` to be overridden due to java type erasure.
- `@ConfigEntry` and `@ConfigCategory` can now use translation keys, if translations are available.
