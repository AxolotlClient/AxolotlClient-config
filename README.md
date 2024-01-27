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

AxolotlClientConfigManager.registerConfig("yourmodsmodid", YourModsConfigClassInstance);

```
