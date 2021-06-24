package games.rednblack.editor.renderer;

import com.artemis.BaseSystem;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import games.rednblack.editor.renderer.box2dLight.RayHandler;
import games.rednblack.editor.renderer.box2dLight.RayHandlerOptions;
import games.rednblack.editor.renderer.commons.IExternalItemType;
import games.rednblack.editor.renderer.factory.EntityFactory;
import games.rednblack.editor.renderer.resources.IResourceRetriever;
import games.rednblack.editor.renderer.resources.ResourceManager;
import games.rednblack.editor.renderer.systems.*;
import games.rednblack.editor.renderer.systems.action.ActionSystem;
import games.rednblack.editor.renderer.systems.render.HyperLap2dRenderer;
import games.rednblack.editor.renderer.utils.CpuPolygonSpriteBatch;

import static games.rednblack.editor.renderer.SceneLoader.BATCH_VERTICES_SIZE;
import static games.rednblack.editor.renderer.SceneLoader.createDefaultShader;

public class SceneConfiguration {

    // SceneLoader - config
    private IResourceRetriever iResourceRetriever;
    private World world;
    private RayHandler rayHandler;
    private boolean cullingEnabled;
    private Array<IExternalItemType> iExternalItemTypes = new Array<>();

    // Artemis World, our Engine - config
    private final Array<SystemData<?>> systems = new Array<>();
    private int expectedEntityCount = 128;
    private boolean alwaysDelayComponentRemoval = false;

    public SceneConfiguration() {

        addSystem(new ParticleSystem());
        addSystem(new LightSystem());
        addSystem(new SpriteAnimationSystem());
        addSystem(new LayerSystem());
        addSystem(new PhysicsSystem());
        addSystem(new CompositeSystem());
        addSystem(new LabelSystem());
        addSystem(new TypingLabelSystem());
        addSystem(new ScriptSystem());
        addSystem(new ActionSystem());
        addSystem(new BoundingBoxSystem());
        addSystem(new CullingSystem());
        addSystem(new HyperLap2dRenderer(new CpuPolygonSpriteBatch(BATCH_VERTICES_SIZE, createDefaultShader())));
        addSystem(new ButtonSystem());

    }

    // For User's Use


    public void setiResourceRetriever(IResourceRetriever iResourceRetriever) {
        this.iResourceRetriever = iResourceRetriever;
    }

    public void setWorld(World world) {
        this.world = world;

        if (containsSystem(PhysicsSystem.class)) {
            PhysicsSystem system = getSystem(PhysicsSystem.class);
            system.setBox2DWorld(this.world);
        }
    }

    public void setRayHandler(RayHandler rayHandler) {
        this.rayHandler = rayHandler;

        if (containsSystem(LightSystem.class)) {
            LightSystem system = getSystem(LightSystem.class);
            system.setRayHandler(this.rayHandler);
            HyperLap2dRenderer renderer = getSystem(HyperLap2dRenderer.class);
            renderer.setRayHandler(this.rayHandler);
        }
    }

    public void setCullingEnabled(boolean cullingEnabled) {
        this.cullingEnabled = cullingEnabled;
    }

    public void addExternalItemType(IExternalItemType itemType) {
        iExternalItemTypes.add(itemType);
        addSystem(itemType.getSystem());
    }

    public void addSystem(BaseSystem system) {
        addSystem(WorldConfigurationBuilder.Priority.NORMAL, system);
    }

    /**
     * Replaces the system if there already exists one of the same type.
     */
    public void addSystem(int priority, BaseSystem system) {
        if (containsSystem(system.getClass())) {
            removeSystem(system.getClass());
        }

        this.systems.add(new SystemData<>(priority, system));
    }

    public boolean containsSystem(Class<? extends BaseSystem> clazz) {
        for (SystemData<?> data : systems) {
            if (data.clazz == clazz) {
                return true;
            }
        }
        return false;
    }

    public void removeSystem(Class<? extends BaseSystem> clazz) {
        // TODO: is there a better way?
        int ind = -1;
        for (int i = 0; i < systems.size; i++) {
            if (systems.get(i).clazz == clazz) {
                ind = i;
                break;
            }
        }
        if (ind != -1)
            systems.removeIndex(ind);
    }

    public <T extends BaseSystem> T getSystem(Class<T> clazz) {
        for (SystemData<?> system : systems) {
            if (system.clazz == clazz)
                return (T) system.system;
        }
        return null;
    }

    public void setExpectedEntityCount(int expectedEntityCount) {
        this.expectedEntityCount = expectedEntityCount;
    }

    public void setAlwaysDelayComponentRemoval(boolean alwaysDelayComponentRemoval) {
        this.alwaysDelayComponentRemoval = alwaysDelayComponentRemoval;
    }

    // For SceneLoader's Use

    IResourceRetriever getiResourceRetriever() {
        if (iResourceRetriever == null) {
            ResourceManager resourceManager = new ResourceManager();
            resourceManager.initAllResources();
            setiResourceRetriever(resourceManager);
        }

        return iResourceRetriever;
    }

    World getWorld() {
        if (world == null)
            setWorld(new World(new Vector2(0, -10), true));

        return world;
    }

    RayHandler getRayHandler() {
        if (rayHandler == null) {
            RayHandlerOptions rayHandlerOptions = new RayHandlerOptions();
            rayHandlerOptions.setGammaCorrection(false);
            rayHandlerOptions.setDiffuse(true);

            RayHandler rayHandler = new RayHandler(world, rayHandlerOptions);
            rayHandler.setAmbientLight(1f, 1f, 1f, 1f);
            rayHandler.setCulling(true);
            rayHandler.setBlur(true);
            rayHandler.setBlurNum(3);
            rayHandler.setShadows(true);

            setRayHandler(rayHandler);
        }

        return rayHandler;
    }

    public boolean isCullingEnabled() {
        return cullingEnabled;
    }

    public Array<IExternalItemType> getiExternalItemTypes() {
        return iExternalItemTypes;
    }

    Array<SystemData<?>> getSystems() {
        if (!cullingEnabled) {
            removeSystem(BoundingBoxSystem.class);
            removeSystem(CullingSystem.class);
        }
        return systems;
    }

    public int getExpectedEntityCount() {
        return expectedEntityCount;
    }

    public boolean isAlwaysDelayComponentRemoval() {
        return alwaysDelayComponentRemoval;
    }

    // For SceneConfiguration's Use

    static class SystemData<T extends BaseSystem> {
        int priority;
        T system;
        Class<?> clazz;

        public SystemData(int priority, T system) {
            this.priority = priority;
            this.system = system;
            this.clazz = system.getClass();
        }
    }

}
