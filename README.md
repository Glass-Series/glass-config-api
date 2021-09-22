# Glass Config API for Minecraft Beta 1.7.3 Client

## Setup

[See the Minecraft Cursed Legacy Website.](https://minecraft-cursed-legacy.github.io/)  
Requires [ModMenu](https://github.com/calmilamsy/ModMenu). Will be made optional in the future to enable server-side usage.

## Usage

1. Create a data class similar to how you would for GSON or Jankson, but all the fields have to be static. Supports Jankson `@Comment`, and it is encouraged to use it so people editing configs manually can read a description of your config value.
2. Make the class implement `HasConfigFields`.
3. Your config is now done. Do NOT copy or store the field values elsewhere unless you want to make the user have to reload their client. Always refer to them directly in your code unless you have to cache the value for some reason.

If you want multiple configs, override `getConfigPath()v` and return the desired name of your config file.

Config files are stored in `.minecraft/config/<modid>/<getConfigPath>.json`. Allows subdirectories.

## Common Issues

[Here.](https://github.com/calmilamsy/BIN-fabric-example-mod#common-issues)

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects. I do ask you to credit me if you use my work though.
