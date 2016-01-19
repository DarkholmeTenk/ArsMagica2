package am2.api.power;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import am2.LogHelper;
import am2.api.ArsMagicaApi;

public final class PowerTypes{

	public static final PowerTypes NONE = new PowerTypes(0, "None", "\u00A7f");
	public static final PowerTypes LIGHT = new PowerTypes(1, "Light", "\u00A7b");
	public static final PowerTypes NEUTRAL = new PowerTypes(2, "Neutral", "\u00A71");
	public static final PowerTypes DARK = new PowerTypes(4, "Dark", "\u00A74");

	private static final ArrayList<PowerTypes> allPowerTypes = new ArrayList<PowerTypes>(){{
		add(LIGHT);
		add(NEUTRAL);
		add(DARK);
	}};

	public static final PowerTypes[] arrLight = new PowerTypes[]{LIGHT};
	public static final PowerTypes[] arrDark = new PowerTypes[]{DARK};
	public static final PowerTypes[] arrNeutral = new PowerTypes[]{NEUTRAL};
	public static final PowerTypes[] arrPos = new PowerTypes[]{LIGHT,NEUTRAL};
	public static final PowerTypes[] arrNeg = new PowerTypes[]{NEUTRAL,DARK};
	public static final PowerTypes[] arrOpp = new PowerTypes[]{LIGHT,DARK};
	public static final PowerTypes[] arrAll = new PowerTypes[]{LIGHT,NEUTRAL,DARK};
	public static final PowerTypes[] arrNone = new PowerTypes[]{NONE};

	private int _id;
	private String _name;
	private String _chatColor;

	private PowerTypes(int ID, String name, String chatColor){
		if ((ID & -ID) != ID){
			throw new InvalidParameterException(String.format("ID must be a bitflag that is a power of 2!  (You used %d)", ID));
		}
		_id = ID;
		_name = name;
		_chatColor = chatColor;
	}

	public static void RegisterPowerType(int id, String name, String chatColor){
		if (getByID(id) == NONE){
			LogHelper.info("Attempted to register power type %s with ID of %d, but that ID is already taken!  The type was NOT registered!", name, id);
		}else{
			allPowerTypes.add(new PowerTypes(id, name, chatColor));
			LogHelper.info("Registered new power type %s with ID %d", name, id);
		}
	}

	public int ID(){
		return _id;
	}

	public String name(){
		return _name;
	}

	public String chatColor(){
		if (ArsMagicaApi.instance.getColourblindMode()){
			return "";
		}
		return _chatColor;
	}

	public static PowerTypes[] all(){
		return arrAll;
	}

	public static PowerTypes getByID(int id){
		for (PowerTypes type : all())
			if (type.ID() == id)
				return type;
		return NONE;
	}
}
