# AxolotlClient-config
The Config lib of AxolotlClient

## Using it in your project

### Include it in your mod
```groovy

repositories {
  maven {
    url "https://moehreag.duckdns.org/maven/releases"
  }
}

dependencies {

  // replace VERSION with the latest version available,
  // check at https://moehreag.duckdns.org/maven/#/releases/io/github/axolotlclient/AxolotlClient-config
  modImplementation include('io.github.axolotlclient:AxolotlClient-config:VERSION')
}

```

<details>
<summary>Beta versions</summary>

```groovy

repositories {
  maven {
    url "https://moehreag.duckdns.org/maven/snapshots"
  }
}

dependencies {

  // replace VERSION with the latest version available,
  // check at https://moehreag.duckdns.org/maven/#/snapshots/io/github/axolotlclient/AxolotlClient-config
  modImplementation include('io.github.axolotlclient:AxolotlClient-config:VERSION')
}

```

</details>

### Registering a config
```java
AxolotlClientConfig.register(ConfigManager manager);
```

### Creating your own options

You'll need to implement the `Option<T>` interface. The `OptionBase<T>` class may be helpful
for many cases. Note though that widgets are specific for each Option/Category implementation meaning
that if you create your own option you will also have to create a widget for it. This does not apply to
options extending the provided types though.

If you want an option to not be saved to the config file, return `null` in `toSerializedValue`
