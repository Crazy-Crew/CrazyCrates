package com.badbones69.crazycrates.v2.utils.quadcrates

import com.badbones69.crazycrates.v2.utils.interfaces.SpiralControl
import org.bukkit.Location
import kotlin.math.cos
import kotlin.math.sin

class QuadCrateSpiralHandler: SpiralControl {
    override fun getSpiralLocationClockwise(center: Location): ArrayList<Location> {
        val world = center.world
        val downwardsDistance = .05
        val expandingRadius = .08
        var y = center.y
        var radius = 0.0
        val amount = 10 //Amount of particles in each circle

        var increaseRadius = 0
        var nextLocation = 0 //The limit of how far the circle goes before reset.

        val increment = 2 * Math.PI / amount //Spacing

        val locations = java.util.ArrayList<Location>()
        for (i in 0..59) {
            val angle = nextLocation * increment
            val x = center.x + radius * cos(angle)
            val z = center.z + radius * sin(angle)
            locations.add(Location(world, x, y, z))
            y -= downwardsDistance
            nextLocation++
            increaseRadius++

            if (increaseRadius == 6) {
                increaseRadius = 0
                radius += expandingRadius
            }

            if (nextLocation == 10) {
                nextLocation = 0
            }
        }
        return locations
    }

    override fun getSpiralLocationCounterClockwise(center: Location): ArrayList<Location> {
        val world = center.world
        val downwardsDistance = .05
        val expandingRadius = .08
        var y = center.y
        var radius = 0.0
        val amount = 10 //Amount of particles in each circle

        var increaseRadius = 0
        var nextLocation = 0 //The limit of how far the circle goes before reset.

        val increment = 2 * Math.PI / amount //Spacing

        val locations = java.util.ArrayList<Location>()
        for (i in 0..59) {
            val angle = nextLocation * increment
            val x = center.x - radius * cos(angle)
            val z = center.z - radius * sin(angle)
            locations.add(Location(world, x, y, z))
            y -= downwardsDistance
            nextLocation++
            increaseRadius++

            if (increaseRadius == 6) {
                increaseRadius = 0
                radius += expandingRadius
            }

            if (nextLocation == 10) {
                nextLocation = 0
            }
        }
        return locations
    }
}