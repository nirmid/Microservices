package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        File input = new File("C:\\Users\\97254\\IdeaProjects\\spl2.0\\src\\main\\resources\\example_input.json");
        try{
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();

            //Extract data
            JsonArray jsonArrayStudents = fileObject.get("Students").getAsJsonArray();
            LinkedList<Student> students = new LinkedList<Student>();
            for (JsonElement studentElement: jsonArrayStudents){
                JsonObject studentObject = studentElement.getAsJsonObject();
                String name = studentObject.get("name").getAsString();
                String department = studentObject.get("department").getAsString();
                String status = studentObject.get("status").getAsString();
                Student.Degree degree;
                if(status.compareTo("MSc") == 0 )
                    degree = Student.Degree.MSc;
                else
                    degree = Student.Degree.PhD;
                LinkedList<Model> models = new LinkedList<Model>();
                for(JsonElement modelElement: jsonArrayStudents){
                    JsonObject modelObject = studentElement.getAsJsonObject();
                    String modelName = modelObject.get("name").getAsString();
                    String typeString = modelObject.get("type").getAsString();
                    int size = modelObject.get("size").getAsInt();
                    Data.Type type;
                    if(typeString.compareTo("Images") == 0)
                        type = Data.Type.Images;
                    else
                        if(typeString.compareTo("Text") == 0)
                            type = Data.Type.Text;
                        else
                            type = Data.Type.Tabular;
                        Data data = new Data(Data.Type.Images,size);
                        models.add(new Model(modelName,data));

                }

                Student student = new Student(name,department,degree,);

            }
        }catch (FileNotFoundException e){}

    }
}
