/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package games.rednblack.editor.renderer.utils;

import com.artemis.*;
import games.rednblack.editor.renderer.components.*;
import games.rednblack.editor.renderer.components.additional.ButtonComponent;
import games.rednblack.editor.renderer.components.label.LabelComponent;
import games.rednblack.editor.renderer.components.label.TypingLabelComponent;
import games.rednblack.editor.renderer.components.light.LightBodyComponent;
import games.rednblack.editor.renderer.components.light.LightObjectComponent;
import games.rednblack.editor.renderer.components.normal.NormalMapRendering;
import games.rednblack.editor.renderer.components.normal.NormalTextureRegionComponent;
import games.rednblack.editor.renderer.components.particle.ParticleComponent;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.components.physics.SensorComponent;
import games.rednblack.editor.renderer.components.sprite.SpriteAnimationComponent;
import games.rednblack.editor.renderer.components.sprite.SpriteAnimationStateComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Component Retriever is a singleton single instance class that initialises list of
 * all component mappers on first access, and provides a retrieval methods to get {@link Component}
 * with provided class from provided {@link Entity} object
 *
 * @author azakhary on 5/19/2015.
 */
public class ComponentRetriever {

    /**
     * single static instance of this class
     */
    private static ComponentRetriever instance;

    /**
     * Unique map of mappers that can be accessed by component class
     */
    private final Map<Class<? extends Component>, BaseComponentMapper<? extends Component>> mappers = new HashMap<>();

    /**
     * an instance to the current World saved here in case a new component is to be added via addMapper()
     */
    private World engine;

    /**
     * Private constructor
     */
    private ComponentRetriever() {

    }

    /**
     * This is called only during first initialisation and populates map of mappers of all known Component mappers
     * it might be a good idea to use Reflections library later to create this list from all classes in components package of runtime, all in favour?
     */
    private void init(World engine) {
        this.engine = engine;

        mappers.put(LightObjectComponent.class, ComponentMapper.getFor(LightObjectComponent.class, engine));

        mappers.put(ParticleComponent.class, ComponentMapper.getFor(ParticleComponent.class, engine));

        mappers.put(LabelComponent.class, ComponentMapper.getFor(LabelComponent.class, engine));
        mappers.put(TypingLabelComponent.class, ComponentMapper.getFor(TypingLabelComponent.class, engine));

        mappers.put(PolygonComponent.class, ComponentMapper.getFor(PolygonComponent.class, engine));
        mappers.put(PhysicsBodyComponent.class, ComponentMapper.getFor(PhysicsBodyComponent.class, engine));
        mappers.put(SensorComponent.class, ComponentMapper.getFor(SensorComponent.class, engine));
        mappers.put(LightBodyComponent.class, ComponentMapper.getFor(LightBodyComponent.class, engine));

        mappers.put(SpriteAnimationComponent.class, ComponentMapper.getFor(SpriteAnimationComponent.class, engine));
        mappers.put(SpriteAnimationStateComponent.class, ComponentMapper.getFor(SpriteAnimationStateComponent.class, engine));

        mappers.put(BoundingBoxComponent.class, ComponentMapper.getFor(BoundingBoxComponent.class, engine));
        mappers.put(CompositeTransformComponent.class, ComponentMapper.getFor(CompositeTransformComponent.class, engine));
        mappers.put(DimensionsComponent.class, ComponentMapper.getFor(DimensionsComponent.class, engine));
        mappers.put(LayerMapComponent.class, ComponentMapper.getFor(LayerMapComponent.class, engine));
        mappers.put(MainItemComponent.class, ComponentMapper.getFor(MainItemComponent.class, engine));
        mappers.put(NinePatchComponent.class, ComponentMapper.getFor(NinePatchComponent.class, engine));
        mappers.put(NodeComponent.class, ComponentMapper.getFor(NodeComponent.class, engine));
        mappers.put(ParentNodeComponent.class, ComponentMapper.getFor(ParentNodeComponent.class, engine));
        mappers.put(TextureRegionComponent.class, ComponentMapper.getFor(TextureRegionComponent.class, engine));
        mappers.put(TintComponent.class, ComponentMapper.getFor(TintComponent.class, engine));
        mappers.put(TransformComponent.class, ComponentMapper.getFor(TransformComponent.class, engine));
        mappers.put(ViewPortComponent.class, ComponentMapper.getFor(ViewPortComponent.class, engine));
        mappers.put(ZIndexComponent.class, ComponentMapper.getFor(ZIndexComponent.class, engine));
        mappers.put(ScriptComponent.class, ComponentMapper.getFor(ScriptComponent.class, engine));

        mappers.put(ShaderComponent.class, ComponentMapper.getFor(ShaderComponent.class, engine));

        mappers.put(ActionComponent.class, ComponentMapper.getFor(ActionComponent.class, engine));
        mappers.put(ButtonComponent.class, ComponentMapper.getFor(ButtonComponent.class, engine));

        mappers.put(NormalMapRendering.class, ComponentMapper.getFor(NormalMapRendering.class, engine));
        mappers.put(NormalTextureRegionComponent.class, ComponentMapper.getFor(NormalTextureRegionComponent.class, engine));
    }

    public static void initialize(World engine) {
        if (instance == null) {
            instance = new ComponentRetriever();

            // Important to initialize during first creation, to populate mappers map
            instance.init(engine);
        }
    }

    /**
     * Short version of getInstance singleton variation, but with private access,
     * as there is no reason to get instance of this class, but only use it's public methods
     *
     * @return ComponentRetriever only instance
     */
    private static synchronized ComponentRetriever self() {
        return instance;
    }

    /**
     * @return returns Map of mappers, for internal use only
     */
    private Map<Class<? extends Component>, BaseComponentMapper<? extends Component>> getMappers() {
        return mappers;
    }

    /**
     * Retrieves Component of provided type from a provided entity
     *
     * @param entity of type Entity to retrieve component from
     * @param type   of the component
     * @param <T>
     * @return Component subclass instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends Component> T get(int entity, Class<T> type) {
        return getMapper(type).get(entity);
//        return (T) self().getMappers().get(type).get(entity);
    }

    public static <T extends Component> BaseComponentMapper<T> getMapper(Class<T> type) {
        return self().engine.getMapper(type);
//        return (BaseComponentMapper<T>) self().getMappers().get(type);
    }

    public static Collection<Component> getComponents(Entity entity) {
        Collection<Component> components = new ArrayList<>();
        for (BaseComponentMapper<? extends Component> mapper : self().getMappers().values()) {
            if (mapper.get(entity) != null) components.add(mapper.get(entity));
        }

        return components;
    }

    /**
     * This is to add a new mapper type externally, in case of for example implementing the plugin system,
     * where components might be initialized on the fly
     *
     * @param type
     */
    public static void addMapper(Class<? extends Component> type) {
        self().getMappers().put(type, ComponentMapper.getFor(type, instance.engine));
    }
}
