package ui.supports;

public class StylePatterns {
	public static final String LABEL_CSS = "-fx-font-size: 12px;"
			+ "-fx-font-weight: bold;"
			+ "-fx-text-fill: #333333;"
			+ "-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );";
	public static final String LABEL_MESSAGE_CSS = "-fx-font-size: 12px;"
			+ "-fx-font-weight: bold;"
			+ "-fx-text-fill: red;"
			+ "-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );";
	public static final String HEADER_CSS = "-fx-font-size: 16px;"
			+ "-fx-font-family: \"Arial Black\";"
			+ "-fx-fill: #818181;"
			+ "-fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.7) , 6, 0.0 , 0 , 2 );";
	public static final String BUTTON_CSS = "-fx-text-fill: white;"
			+ "-fx-font-family: \"Arial Narrow\";"
			+ "-fx-font-weight: bold;"
			+ "-fx-background-color: linear-gradient(#61a2b1, #2A5058);"
			+ "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";
	public static final String BUTTON_HOVERD_CSS = "-fx-background-color: linear-gradient(#2A5058, #61a2b1);"
			+ "";
	public static final String COMBO_BOX_CSS = "-fx-text-fill: white;"
			+ "-fx-font-family: \"Arial Narrow\";"
			+ "-fx-font-weight: bold;"
			+ "-fx-background-color: linear-gradient(#B6F2FF, #8EE2F4);"
			+ "-fx-border-width: 0.2;"
            + "-fx-border-style: solid inside;"
            + "-fx-border-color:#3F646D;";
	
	public static final String TABLE_BUTTON = "-fx-padding: 3 3 3 3;"
			+ " -fx-background-insets: 0,2 2 2 2, 4 2 4 2, 5 2 5 2;"
			+ "-fx-background-radius: 4;"
			+ "-fx-background-color: "
			+ "linear-gradient(from 0% 93% to 0% 100%, #8056DE 0%, #986CF8 100%),"
			+ "#57389C,"
			+ "#7656BE,"
			+ "radial-gradient(center 50% 50%, radius 100%, #B79EEF, #8552F6);"
			+ "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );"
			+ "-fx-font-weight: bold;"
			+ "-fx-font-size: 10px;";
	
	public static final String TABLE_BUTTON_PRESS = "-fx-padding: 3 3 3 3;"
			+ " -fx-background-insets: 0,4 4 4 4, 6 4 6 5, 7 4 7 4;"
			+ "-fx-background-radius: 5;"
			+ "-fx-background-color: "
			+ "linear-gradient(from 0% 93% to 0% 100%, #8056DE 0%, #986CF8 100%),"
			+ "#69539A,"
			+ "#7656BE,"
			+ "radial-gradient(center 50% 50%, radius 100%, #DCD1F5, #AB87F9);"
			+ "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.75) , 4,0,0,1 );"
			+ "-fx-font-weight: bold;"
			+ "-fx-font-size: 9px;"
			+ "-fx-text-fill: #504D56;";
}
