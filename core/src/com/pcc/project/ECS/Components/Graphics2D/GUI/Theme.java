package com.pcc.project.ECS.Components.Graphics2D.GUI;

public enum Theme {
    Blue, Green, Grey, Red, Yellow;

    public static String getString ( Theme theme ) {
        switch ( theme ) {
            case Red: return "red";
            case Blue: return "blue";
            case Green: return "green";
            case Grey: return "grey";
            case Yellow: return "yellow";

            default: return null;
        }
    }
}
