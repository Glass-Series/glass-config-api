[Quick Usage](#quick-usage)  
[Categories](#categories)  
[Annotations](#annotations)  
[Multiplayer Syncing](#multiplayer-syncing)  
[Multiple Config Pages](#multiple-config-pages)  
[IMPORTANT NOTES](#important-notes)

# Quick Usage

1. Create an empty non-static class somewhere in your project. We will be using this later.
2. Create an instantiated `public static final` field for your first config screen and annotate it with `@ConfigRoot`, make sure the field's type is the class you just created. **Do not** put it inside your newly made class. (e.g. `public static final ExampleConfigClass config = new ExampleConfigClass()`)
3. Fill out the required `value` and `visibleName` parameters on the [annotation](#Annotations). Value is the name of your config file. (e.g. `@ConfigRoot(value = "config", visibleName = "My Config GUI")`)
4. Go into your `fabric.mod.json` and add the snippet below inside your `entrypoints` section:
```json
"gcapi": [
  "<path.to.the.class.containing.the.field.here>"
]
```
5. Now let's go back to the empty class you made earlier.
6. Create some fields with the type you want ([Valid Types](Types)) and annotate them with `@ConfigEntry`, filling out the value parameter with what you'd like the user to see your option called in the config GUI. (e.g. `@ConfigEntry(name = "My Config Field") public String myField = ""`)
7. Done! You can use the field in your code, GCAPI will handle loading and syncing logic for you.

# Categories
To make a category, you will need to:
1. Create a class that will represent your category and fill it with fields like normal, including the required [annotations](#Annotations).
2. In the class where you want the category, add a `@ConfigCategory` annotation, and fill out the required value parameter.
3. Done! You can have categories inside of categories if you so wish to.

# Annotations
| Annotation      | Description                                        |           Target            |
|-----------------|----------------------------------------------------|:---------------------------:|
| @ConfigRoot     | Used by GCAPI to find your mod configs in a class. | static config object fields |
| @ConfigEntry    | Holds all the metadata of a config entry.          |        config fields        |
| @ConfigCategory | Holds all the metadata of a config category.       |   config category fields    |

# Multiplayer Syncing
Just set the `multiplayerSynced` `ConfigEntry`/`ConfigCategory` annotation value to `true` on the field or categories you want synced with the server.

# Multiple Config Pages
Just add another `@ConfigRoot` field, and if it's in a different class from the first one, add it's class to the `entrypoints` section in `fabric.mod.json` too.

Buttons will be added at the top of the config page for your mod for navigating these.

You are able to set a priority for these pages, with a lower number meaning it will appear sooner in the list.

# Enums
In 2.0.0, you can now use enums to create cycling config values.

The catch is that you need to create your enum type, and provide a set of simple config factories.

See [ExampleConfigEnum](../../src/test/java/net/modificationstation/sltest/gcapi/ExampleConfigEnum.java) and [ExampleConfigEnumFactories](../../src/test/java/net/modificationstation/sltest/gcapi/ExampleConfigEnumFactories.java) on how to do it.

# Generated Configs
In 2.0.0, you can now dynamically supply config categories and fields.

Make your config category class, implement `GeneratedConfig`.

# IMPORTANT NOTES

- Use `GCAPI.reloadConfig` to manually set config values. This is to ensure mods are notified properly and can post-process things.
- Servers are able to send whatever configs they want, so long as the config field on the client is MP syncable. The server doesn't even need to have the mod installed. This would require emulating GCAPI's config export code to do so, though.
