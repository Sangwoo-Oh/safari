/**
 * Abstract base class representing a general entity with a 3-dimensional position.
 * Used as a superclass for any object within the game world that requires spatial representation.
 */
package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;

public abstract class Entity {
    protected GameModel gameModel;
    /**
     * The 3D position vector of this entity in the game world.
     */
    protected Vector3 position;

    /**
     * Constructs an entity at the specified (x, y, z) coordinates.
     *
     * @param x The X-coordinate of the entity.
     * @param y The Y-coordinate of the entity.
     * @param z The Z-coordinate of the entity.
     */
    public Entity(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
    }

    /**
     * Retrieves the current position of this entity as a Vector3.
     *
     * @return A Vector3 object representing the entity's position.
     */
    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getPositionCPY(){
        return position.cpy();
    }

    /**
     * Sets the entity's position using a Vector3 object.
     *
     * @param position The new position as a Vector3.
     */
    public void setPosition(Vector3 position) {
        this.position.set(position);
    }

    /**
     * Sets the entity's position by specifying individual float coordinates.
     *
     * @param x The new X-coordinate.
     * @param y The new Y-coordinate.
     * @param z The new Z-coordinate.
     */
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    /**
     * Retrieves the X-coordinate of the entity.
     *
     * @return The current X-coordinate.
     */
    public float getX() {
        return position.x;
    }

    /**
     * Retrieves the Y-coordinate of the entity.
     *
     * @return The current Y-coordinate.
     */
    public float getY() {
        return position.y;
    }

    /**
     * Retrieves the Z-coordinate of the entity.
     *
     * @return The current Z-coordinate.
     */
    public float getZ() {
        return position.z;
    }


    /**
     * Provides a string representation of the entity, including its position.
     * Useful for debugging purposes.
     *
     * @return A formatted string describing the entity's position.
     */
    @Override
    public String toString() {
        return "Entity at (" + position.x + ", " + position.y + ", " + position.z + ")";
    }
}
