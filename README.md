# KubeJS Offline

## Goal

To provide dynamic javadoc-like documentation for use with KubeJS scripts.
The reason we can't just use the javadoc is because every user who makes
a modpack uses different mods, and javadoc is intended for use with
the source code of the mod itself. Therefor we need to generate this information
at runtime.

## How it works

It works by using the Reflections Library to scan the classpath for classes.
Then I use a similar method to a web crawler to find all classes that were possibly missed.
From there, I generate a JSON file that contains all the information I need to generate
a webpage. I then create a webpage using the JSON file and a handful of JS files to
dynamically generate the webpage for each class.

# GitHub Pages

I have standard KubeJS installations documented on GitHub Pages.
If you plan to use this mod with other mods, you can generate a more accurate version
by including the mod in your pack and running the command `/kubejs offline`.
You can find the standard installation instructions below:

#### Forge 1.20.1: https://hunter19823.github.io/kubejsoffline/1.20.1/forge/

#### Fabric 1.20.1: https://hunter19823.github.io/kubejsoffline/1.20.1/fabric/

#### Forge 1.19.2: https://hunter19823.github.io/kubejsoffline/1.19.2/forge/

#### Fabric 1.19.2: https://hunter19823.github.io/kubejsoffline/1.19.2/fabric/

#### Forge 1.18.2: https://hunter19823.github.io/kubejsoffline/1.18.2/forge/

#### Fabric 1.18.2: https://hunter19823.github.io/kubejsoffline/1.18.2/fabric/
