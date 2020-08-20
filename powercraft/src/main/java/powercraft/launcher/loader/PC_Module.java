package powercraft.launcher.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import net.minecraft.entity.Entity;
import powercraft.launcher.PC_Property;

/**
 * 
 * A PowerCraft Module need this Annontation to be detected as module
 * 
 * @author XOR
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PC_Module {

	/**
	 * gets the name of the module
	 * 
	 * @return name
	 */
	public String name();

	/**
	 * gets the version of the module
	 * 
	 * @return version
	 */
	public String version();

	/**
	 * gets the dependencies of the module
	 * 
	 * @return dependencies
	 */
	public String dependencies() default "";

	/**
	 * 
	 * used on a field to get the references to modules
	 * 
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface PC_Instance {
		/**
		 * name of the module
		 * 
		 * @return module name
		 */
		public String module() default "";
	}

	/**
	 * 
	 * Function for Pre Init
	 * 
	 * @return Function output param: void>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_PreInit {
	}

	/**
	 * 
	 * Function for Init
	 * 
	 * @return Function output param: void>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_Init {
	}

	/**
	 * 
	 * Function for Post Init
	 * 
	 * @return Function output param: void>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_PostInit {
	}

	/**
	 * 
	 * Function for Init Properties
	 * 
	 * @param config Function input param: {@link PC_Property}
	 * @return Function output param: void>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_InitProperties {
	}

	/**
	 * 
	 * TODO: add JavaDoc
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_RegisterEventHandlers {
	}

	/**
	 * 
	 * Function for register entities
	 * 
	 * @param list Function input param:
	 *             {@link List}<{@link PC_Struct2}<{@link Class}< ? extends
	 *             {@link Entity}>, {@link Integer}>>
	 * @return Function output param: {@link List}<{@link PC_Struct2}<{@link Class}<
	 *         ? extends {@link Entity}>, {@link Integer}>>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_InitEntities {
	}

	/**
	 * 
	 * Function for init recipes
	 * 
	 * @param list Function input param: {@link List}<{@link PC_IRecipe}>
	 * @return Function output param: {@link List}<{@link PC_IRecipe}>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_InitRecipes {
	}
	
	/*
	 * TODO: Add Java Doc
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_Init3DRecipes {
	}

	/**
	 * 
	 * Function for init data handlers
	 * 
	 * @param list Function input param:
	 *             {@link List}<{@link PC_Struct2}<{@link String},
	 *             {@link PC_IDataHandler}>>
	 * @return Function output param:
	 *         {@link List}<{@link PC_Struct2}<{@link String},
	 *         {@link PC_IDataHandler}>>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_InitDataHandlers {
	}

	/**
	 * 
	 * Function for init packet handlers
	 * 
	 * @param list Function input param:
	 *             {@link List}<{@link PC_Struct2}<{@link String},
	 *             {@link PC_IPacketHandler}>>
	 * @return Function output param:
	 *         {@link List}<{@link PC_Struct2}<{@link String},
	 *         {@link PC_IPacketHandler}>>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_InitPackets {
	}

	/**
	 * 
	 * Function for register containers
	 * 
	 * @param list Function input param:
	 *             {@link List}<{@link PC_Struct2}<{@link String}, {@link Class}< ?
	 *             extends {@link PC_GresBaseWithInventory}>>>
	 * @return Function output param:
	 *         {@link List}<{@link PC_Struct2}<{@link String}, {@link Class}< ?
	 *         extends {@link PC_GresBaseWithInventory}>>>
	 * @author XOR
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PC_RegisterContainers {
	}

}
