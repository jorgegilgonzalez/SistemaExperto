package com.jorge.sistemaexperto;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private ChoiceBox<String> sexo1;
    @FXML
    private ChoiceBox<String> sexo2;
    @FXML
    private ChoiceBox<String> preferencia1;
    @FXML
    private ChoiceBox<String> preferencia2;

    List<String> sexos = Arrays.asList("Hombre", "Mujer");
    List<String> preferencias = Arrays.asList("Mismo sexo", "Sexo contrario", "Indistinto");

    @FXML
    private Label resultadoTxt;
    @FXML
    private Label compatibilidad;
    @FXML
    private TextField edad1;
    @FXML
    private TextField edad2;
    @FXML
    private CheckBox deporte1;
    @FXML
    private CheckBox leer1;
    @FXML
    private CheckBox cocinar1;
    @FXML
    private CheckBox viajar1;
    @FXML
    private CheckBox hijos1;
    @FXML
    private CheckBox salir1;
    @FXML
    private CheckBox deporte2;
    @FXML
    private CheckBox leer2;
    @FXML
    private CheckBox cocinar2;
    @FXML
    private CheckBox viajar2;
    @FXML
    private CheckBox hijos2;
    @FXML
    private CheckBox salir2;

    @FXML
    protected void onCalcularButtonClick() {
        resultadoTxt.setText("Rellena todos los campos");

        //RECOGEMOS LOS DATOS

        String sexoC1 = sexo1.getValue();
        String sexoC2 = sexo2.getValue();
        String preferenciaC1 = preferencia1.getValue();
        String preferenciaC2 = preferencia2.getValue();
        Integer edadC1 = Integer.parseInt(edad1.getText());
        Integer edadC2 = Integer.parseInt(edad2.getText());
        Boolean deporteC1 = deporte1.isSelected();
        Boolean deporteC2 = deporte2.isSelected();
        Boolean leerC1 = leer1.isSelected();
        Boolean leerC2 = leer2.isSelected();
        Boolean cocinarC1 = cocinar1.isSelected();
        Boolean cocinarC2 = cocinar2.isSelected();
        Boolean viajarC1 = viajar1.isSelected();
        Boolean viajarC2 = viajar2.isSelected();
        Boolean hijosC1 = hijos1.isSelected();
        Boolean hijosC2 = hijos2.isSelected();
        Boolean salirC1 = salir1.isSelected();
        Boolean salirC2 = salir2.isSelected();


        //MOTOR DE INFERENCIA

           /*
    Primero se ve si es posible el amor o sólamente amistad, atendiendo a su
    sexo, orientacion sexual y edad, de tal manera que hay unos caminos en el árbol
    de decisión excluyentes, y una variable, la edad, que toma pesos diferentes en
    la decisión según la diferencia de edad entre ambos candidatos.
    */

        //evalua si es posible el amor entre dos personas atendiendo a su sexo y preferencia
        boolean amor = esAmor(sexoC1, sexoC2, preferenciaC1, preferenciaC2);

        //si es posible el amor, todavia la diferencia de edad puede ser un impedimento
        if (amor) {
            amor = esDiferenciaEdadInconveniente(edadC1, edadC2, 10, amor);
        }


        //mostramos el resultado
        if (amor) {
            resultadoTxt.setText("amor");
        } else {
            resultadoTxt.setText("podría ser amistad");
        }

        //evaluamos la compatibilidad según sus gustos aficiones y objetivos

        int puntuacion = evaluarCompatibilidad(deporteC1, deporteC2, leerC1, leerC2, cocinarC1, cocinarC2, viajarC1, viajarC2, hijosC1, hijosC2, salirC1, salirC2, amor);

        //una vez obtenido el resultado damos una respuesta a los usuarios
        if(amor){
            compatibilidad.setText(resultadoCompatibilidadAmor(puntuacion));
        } else {
            compatibilidad.setText(resultadoCompatibilidadAmistad(puntuacion));
        }
    }


    @FXML
    protected void limpiarCamposButtonClick() {
        resultadoTxt.setText("Campos borrados");
        compatibilidad.setText("Nivel de compatibilidad");
        edad1.setText("");
        edad2.setText("");
        hijos1.setSelected(false);
        hijos2.setSelected(false);
        deporte1.setSelected(false);
        deporte2.setSelected(false);
        viajar1.setSelected(false);
        viajar2.setSelected(false);
        leer1.setSelected(false);
        leer2.setSelected(false);
        cocinar1.setSelected(false);
        cocinar2.setSelected(false);
        salir1.setSelected(false);
        salir2.setSelected(false);
        sexo1.getSelectionModel().clearSelection();
        sexo2.getSelectionModel().clearSelection();
        preferencia1.getSelectionModel().clearSelection();
        preferencia2.getSelectionModel().clearSelection();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sexo1.getItems().addAll(sexos);
        sexo2.getItems().addAll(sexos);
        preferencia1.getItems().addAll(preferencias);
        preferencia2.getItems().addAll(preferencias);


        edad1.setText("25");
        edad2.setText("25"); //Valores iniciales

        //hace que solo acepten valores enteros
        edad1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                edad1.setText(oldValue);
            }
        });

        edad2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                edad2.setText(oldValue);
            }
        });

    }

    //regla que evalua la compatibilidad a nivel de sexo y orientacion sexual
    public static boolean esAmor(String sexoC1, String sexoC2, String preferenciaC1, String preferenciaC2) {


        // Si son del mismo sexo y aunque uno busque el mismo sexo al otro le da igual, ¡es amor!
        if (sexoC1.equals(sexoC2) && ((preferenciaC1.equals("Mismo sexo") && preferenciaC2.equals("Indistinto")) || (preferenciaC1.equals("Indistinto") && preferenciaC2.equals("Mismo sexo")))) {
            return true;
        }

        // Si ambos son del mismo sexo y ambos buscan el mismo sexo o indistinto, ¡es amor!
        if (sexoC1.equals(sexoC2) && !preferenciaC1.equals("Sexo contrario") && preferenciaC1.equals(preferenciaC2)) {
            return true;
        }

        // Si son de diferente sexo y ambos buscan el sexo contrario, ¡es amor!
        if (!sexoC1.equals(sexoC2) && preferenciaC1.equals("Sexo contrario") && preferenciaC2.equals(preferenciaC1)) {
            return true;
        }

        // Si son de diferente sexo y aunque uno busque el sexo contrario al otro le da igual, ¡es amor!
        if (!sexoC1.equals(sexoC2) && ((preferenciaC1.equals("Sexo contrario") && preferenciaC2.equals("Indistinto")) || (preferenciaC1.equals("Indistinto") && preferenciaC2.equals("Sexo contrario")))) {
            return true;
        }

        // Si ambos son bisexuales, ¡es amor!
        if (preferenciaC1.equals("Indistinto") && preferenciaC2.equals("Indistinto")) {
            return true;
        }

        // En cualquier otro caso, no hay compatibilidad amorosa pero podria haber una bonita amistad

        return false;
    }

    //regla que evalúa si la diferencia de edad es un obstáculo para una relación romántica,
    // la regla es ajustable de tal manera que se puede establecer una diferencia máxima que aumenta a medida que las edades
    //de ambos son más elevadas, porque una diferencia de edad de 15 años cuando tienes 80 no es lo mismo que cuando tienes 18
    public static boolean esDiferenciaEdadInconveniente(int edad1, int edad2, int diferenciaMaxima, boolean esAmor) {

        // Calcular la media de las edades
        int edadMedia = (edad1 + edad2) / 2;

        //Si no es amor, puede ser amistad y no es tan importante la diferencia de edad
        //se crea esta regla por si se desea usar para ajustan el nivel de compatibilidad posteriormente. De momento no se usa.
        if (!esAmor) {
            edadMedia = edadMedia + 20;
        }

        // Calcular la diferencia de edad
        int diferenciaEdad = Math.abs(edad1 - edad2);

        // Calcular la diferencia máxima proporcional
        double diferenciaMaximaProporcional = diferenciaMaxima * (0.8 + (edadMedia / 100.0));

        // Devolver true si la diferencia de edad es menor o igual a la diferencia máxima proporcional
        return diferenciaEdad <= diferenciaMaximaProporcional;
    }

    //Reglas complejas que evalúan las interacciones entre diferentes aficiones comunes no comunes y dependiendo de si estamos evaluando amor o amistad
    public int evaluarCompatibilidad(Boolean deporteC1, Boolean deporteC2, Boolean leerC1, Boolean leerC2, Boolean cocinarC1, Boolean cocinarC2, Boolean viajarC1, Boolean viajarC2, Boolean hijosC1, Boolean hijosC2, Boolean salirC1, Boolean salirC2, Boolean esAmor) {

        int compatibilidad = 0;

        //si a ambos les apasiona el deporte, es un gran punto a favor (+2), pero si a uno si y al otro no, es un impedimiento (-1). Si a ninguno les gusta, no se molestaran con el tema (+1)

        if (deporteC1 && deporteC2) compatibilidad += 2;
        if (deporteC1 != deporteC2) compatibilidad--;
        if (!deporteC1 && !deporteC2) compatibilidad++;


        //Si a ambos les gusta leer, es un buen punto de encuentro (+1) pero si a uno si y al otro no, le verá con malos ojos (-2), si a ninguno le gusta, les da igual (+0)

        if (leerC1 && leerC2) compatibilidad++;
        if (leerC1 != leerC2) compatibilidad -= 2;

        //Si a ambos les encanta la cocina, pasaran buenos ratos (+2) si a uno si y al otro no, da un poco igual, si a ninguno le gusta se acabaran aburriendo de comer siempre lo mmismo (-1)
        //OJO! si a ambos les gusta el deporte y cocinar es un gran plus, se ciudaran con un extra (+1) Y si ya encima a los dos les va leer sobre el tema, otro +1

        if (cocinarC1 && cocinarC2) {
            compatibilidad += 2;
        } else if (deporteC1 && deporteC2) {
            compatibilidad++;
            if (leerC1 && leerC2) compatibilidad++;
        }

        if (!cocinarC1 && !cocinarC2) {
            compatibilidad--;
        }
        //sin embargo, si a uno no le gusta ni cocinar sano ni el deporte y al otro si y se cuida, lo tenemos mal -2 excepto sin son amigos, que tampoco es tan importante (+0)

        if ((!cocinarC1.equals(cocinarC2) && !deporteC1.equals(deporteC2))) {
            if (esAmor) {
                compatibilidad -= 2;
            }
        }


        //Si a ambos les encanta viajar que bien (+2) si a ninguno le gusta, son caseros, pasan un poco +0, pero como a uno le guste y al otro no... (-2).. excepto si son amigos, no vais juntos a todos lados.(+0)

        if (viajarC1 && viajarC2) compatibilidad += 2;
        if (!viajarC1.equals(viajarC2) && esAmor) compatibilidad -= 2;

        //en caso de amor, si ambos quieren hijos, eso une! (+2), pero si ninguno quiere, tambien (+1), eso si, como no est'en deacuerdo en esto, es dificil de superar. (-4)

        if (esAmor) {
            if (hijosC1 && hijosC2) {
                compatibilidad += 2;
                if (!hijosC1 && hijosC2)
                    compatibilidad++;
                if (!hijosC1.equals(hijosC2)) {
                    compatibilidad -= 4;
                    //claro está, esto no es lo mismo que si son amigos, en cuyo caso une tener nenes de la misma edad (+1) y es dificil hacer planes si uno tiene y el otro no (-1)

                } else {
                    if (hijosC1 && hijosC2) compatibilidad++;
                    if (!hijosC1.equals(hijosC2)) compatibilidad--;
                }
            }
        }

        //ademas , cuantas más aficiones haya en comun, más actividades podréis realizar y cuantas menos más os aburriréis. Esto es la afinidad general.

        Boolean[] aficionesC1 = {deporteC1, leerC1, cocinarC1, viajarC1, hijosC1, salirC1};
        Boolean[] aficionesC2 = {deporteC2, leerC2, cocinarC2, viajarC2, hijosC2, salirC2};

        int numeroAficionesComunes = 0;

        // Recorrer las aficiones de la persona 1
        for (int i = 0; i < aficionesC1.length; i++) {

            // Si la afición de la persona 1 coincide con la de la persona 2, aumentar el contador
            if (aficionesC1[i] == aficionesC2[i]) {
                numeroAficionesComunes++;
            }
        }

        compatibilidad = compatibilidad + numeroAficionesComunes;

        return compatibilidad;
    }

    //en función de los resultados de puntuación devuelve niveles de compatibilidad en caso de amistad
    private String resultadoCompatibilidadAmistad (int puntuacion){

            String nivelAmistad;

            switch (puntuacion) {
                case 0,1,2:
                    nivelAmistad = "No teneis mucho futuro";
                    break;
                case 3,4:
                    nivelAmistad = "Para unas cañas un dia";
                    break;
                case 5,6:
                    nivelAmistad = "Podríais daros una oportunidad";
                    break;
                case 7,8:
                    nivelAmistad = "Podéis llegar a ser buenos colegas";
                    break;
                case 9,10:
                    nivelAmistad = "Te presento a un gran amigo";
                    break;
                case 11,12,13,14:
                    nivelAmistad = "Amistad legendaria";
                    break;
                default:
                    nivelAmistad = "Mejor ni os saludéis si no quereis acabar a golpes";
            }

            return nivelAmistad;
        }

    //en función de los resultados de puntuación devuelve niveles de compatibilidad en caso de amor
    private String resultadoCompatibilidadAmor (int puntuacion){

        String nivelAmor;

        switch (puntuacion) {
            case 0,1,2:
                nivelAmor = "Como el perro y el gato";
                break;
            case 3,4:
                nivelAmor = "Que aburrimiento";
                break;
            case 5,6:
                nivelAmor = "Podríais pasar buenos ratos pero pocos";
                break;
            case 7,8:
                nivelAmor = "Aquí hay feeling";
                break;
            case 9,10:
                nivelAmor = "Que bonito es el amor!";
                break;
            case 11,12,13,14:
                nivelAmor = "Se escribirán libros sobre lo vuestro";
                break;
            case 15,16,17,18:
                nivelAmor = "Si os dejáis escapar el mundo acabará.";
                break;
            default:
                nivelAmor = "Os acabaríais odiando";
        }

        return nivelAmor;
    }

}


