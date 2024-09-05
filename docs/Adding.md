# Adding Glass Config API

You want to use GCAPI to handle your mod config? Great!

You already have GCAPI in your workspace if you have [StationAPI](../../wiki/Setting-up-workspace), so skip ahead to [Using](Using.md) if you are.

Add this line to your dependencies block in your build.gradle file:

<details>
<summary>Groovy (build.gradle)</summary>

```groovy
modImplementation "net.glasslauncher.mods.GlassConfigAPI:3.0.0"
```
</details>
<details>
<summary>Kotlin (build.gradle.kts)</summary>

```kotlin
modImplementation("net.glasslauncher.mods.GlassConfigAPI:3.0.0") 
```
</details>

For the next steps, see [Using](Using.md).
