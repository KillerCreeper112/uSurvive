
plugins {
    java
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadow)
}

version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    compileOnly(files(
        "E:\\Plugins\\YO\\CruxCore\\build\\libs\\CruxCore-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxTeleport\\build\\libs\\CruxTeleport-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxCore\\run\\plugins\\CrazyAdvancementsAPI-2.1.19.jar",
        "D:\\MythicMobs-5.6.2.jar",
        "D:\\ModelEngine-R4.0.9.jar",
        "E:\\Plugins\\YO\\CruxCurrency\\build\\libs\\CruxCurrency-1.0.jar",
        "E:\\Plugins\\YO\\CruxClaims\\build\\libs\\CruxClaims-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxFriends\\build\\libs\\CruxFriends-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxParty\\build\\libs\\CruxParty-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxWarps\\build\\libs\\CruxWarps-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxRewards\\build\\libs\\CruxRewards-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxPlayerBlocks\\build\\libs\\CruxEntityBlocks-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxCharms\\build\\libs\\CruxCharms-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxShops\\build\\libs\\CruxShops-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxMagic\\build\\libs\\CruxMagic-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxEnchantsOverhaul2.0\\build\\libs\\CruxEnchantsOverhaul-2.0.jar",
        "E:\\Plugins\\YO\\CruxAbyss\\build\\libs\\CruxAbyss-1.0.jar",
        "E:\\Plugins\\YO\\CruxChat\\build\\libs\\CruxChat-1.0-all.jar",

        "E:\\Plugins\\YO\\CruxCore\\run\\plugins\\QuickShop-Hikari-6.2.0.8.jar",

        "E:\\Plugins\\YO\\CruxCore\\run\\plugins\\ProtocolLib.jar",
        "E:\\Plugins\\YO\\CruxCore\\run\\plugins\\CrazyAdvancementsAPI-2.21.9.jar",
    ))

    implementation("fr.skytasul", "glowingentities", "1.4.9")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")

    compileOnly(fileTree("libs"){
        include("*.jar")
    })
    compileOnly(files(
        "E:\\Plugins\\YO\\CruxCore\\run\\plugins\\MobFarmManager3.0.4.3.jar"
    ))
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks{
    assemble{
        //dependsOn(reobfJar)
        dependsOn(shadowJar)
    }
}

allprojects{

    plugins.apply("java")

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        systemProperty("file.encoding", "UTF-8")
    }

    tasks.withType<Javadoc>{
        options.encoding = "UTF-8"
    }
}













