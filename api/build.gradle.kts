plugins {
    id("event.base-conventions")
}

dependencies {
    api(libs.fastutil)
    api(libs.common)

    api(platform(libs.adventure.bom))
    api("net.kyori:adventure-key")
}
