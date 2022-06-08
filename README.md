# Crazy Crates
Source Code for Crazy Crates

## Build Status:

[![Build Status](https://jenkins.badbones69.com/job/Crazy-Crates/badge/icon)](https://jenkins.badbones69.com/job/Crazy-Crates/)

## Latest Version:

[![Latest Version](https://img.shields.io/badge/Latest%20Version-1.10-blue)](https://github.com/badbones69/Crazy-Crates/releases/latest)

## Jenkins:

https://jenkins.badbones69.com/job/Crazy-Crates/

## Bulk Messages

You can add special commands and messages for all crates, so when someone tries to open multiple at the same time, they
don't get spammed with repeated messages.

You need to setup few things first.

First, set the Crate.Crate-Bulk-Display-Name to the name you want to display for the crate on the bulk message.

Second, for all the rewards you must set the Crate-Bulk-Message to the message you want to display when opening multiple
of the same type, for example:

Crate-Bulk-Message: ```"&7&l10.000 Tokens &a&lx%amount%"```

You can also add Bulk-Commands instead of Commands for specific commands when opening multiple crates

## Nexus:

https://nexus.badbones69.com/#browse/browse:maven-releases:me%2Fbadbones69%2Fcrazycrates-plugin

## Maven:

```xml
<repository>
    <id>nexus</id>
    <url>https://nexus.badbones69.com/repository/maven-releases/</url>
</repository>

<dependency>
    <groupId>me.badbones69</groupId>
    <artifactId>crazycrates-plugin</artifactId>
    <version>{Latest Version}</version>
</dependency>
```

##Bulk Messages:


