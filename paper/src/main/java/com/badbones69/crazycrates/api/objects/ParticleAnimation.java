package com.badbones69.crazycrates.api.objects;


public class ParticleAnimation {

    private final String identifier;
    private final String particleName;
    private final String particleAnimation;
    private final int color;

    public ParticleAnimation(String identifier, String Animation, String Particle, int Color) {
        this.identifier = identifier;
        this.particleAnimation = Animation;
        this.particleName = Particle;
        this.color = Color;
    }

    /**
     * @return String identifier for the particle.
     */
    public String getIdentifier() { return identifier; }

    /**
     * @return The selected animation.
     */
    public String getAnimation() { return particleAnimation; }

    /**
     * @return The particle used in the animation.
     */
    public String getParticle() { return particleName; }

    /**
     * @return Hex colour that the particles should be.
     */
    public int getColor() {
        return color;
    }
}
