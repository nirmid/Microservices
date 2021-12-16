package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
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

            //Extract Students
            JsonArray jsonArrayStudents = fileObject.get("Students").getAsJsonArray();
            LinkedList<StudentService> studentServices = new LinkedList<StudentService>();
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

                // Extract Models
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
                Student student = new Student(name,department,degree,models);
                for(Model model: models)
                    model.setStudent(student);
                studentServices.add(new StudentService(name,student));
            }

            // Extract GPUS
            LinkedList<GPU> gpus = new LinkedList<GPU>();
            LinkedList<GPUService> GPUServices = new LinkedList<GPUService>();
            JsonArray jsonArrayGpus = fileObject.get("GPUS").getAsJsonArray();
            int gpuCounter = 0;
            for(JsonElement gpuElement: jsonArrayGpus){
                GPU gpu;
                JsonObject gpuObject = gpuElement.getAsJsonObject();
                String gpuType = gpuObject.getAsString();
                if(gpuType.compareTo("RTX3090") == 0)
                    gpu = new GPU(GPU.Type.RTX3090);
                else {
                    if (gpuType.compareTo("RTX2080") == 0)
                        gpu = new GPU(GPU.Type.RTX2080);
                    else
                        gpu = new GPU(GPU.Type.GTX1080);
                }
                gpus.add(gpu);
                GPUServices.add(new GPUService("GPU"+gpuCounter,gpu));
                gpuCounter = gpuCounter +1;

            }

            // Extract CPUS
            LinkedList<CPU> cpus = new LinkedList<CPU>();
            LinkedList<CPUService> CPUServices = new LinkedList<CPUService>();
            JsonArray jsonArrayCpus = fileObject.get("CPUS").getAsJsonArray();
            int cpuCounter = 0;
            for(JsonElement cpuElement: jsonArrayCpus) {
                JsonObject cpuObject = cpuElement.getAsJsonObject();
                int cores = cpuObject.getAsInt();
                CPU cpu = new CPU(cores);
                cpus.add(cpu);
                CPUServices.add(new CPUService("CPU"+gpuCounter,cpu));
                cpuCounter = cpuCounter +1;
            }

            // Extract Conferences
            JsonArray jsonArrayConference = fileObject.get("Conferences").getAsJsonArray();
            LinkedList<ConfrenceInformation> conferences = new LinkedList<ConfrenceInformation>();
            LinkedList<ConferenceService> conferenceServices = new LinkedList<ConferenceService>();
            for(JsonElement conferenceElement: jsonArrayConference) {
                JsonObject conferenceObject = conferenceElement.getAsJsonObject();
                String name = conferenceObject.get("name").getAsString();
                int date = conferenceObject.get("date").getAsInt();
                ConfrenceInformation confrenceInformation = new ConfrenceInformation(name,date);
                conferences.add(confrenceInformation);
                conferenceServices.add(new ConferenceService(name,confrenceInformation));
            }

            //Extract TickTime
            int tickTime = fileObject.get("TickTime").getAsInt();
            int duration = fileObject.get("Duration").getAsInt();
            TimeService time = new TimeService(duration,tickTime);
            Thread timeService = new Thread(time);

            //GPU Threads
            for(GPUService gpuService: GPUServices){
                Thread gpuThread = new Thread(gpuService);
                gpuThread.start();
            }

            //CPU Threads
            for(CPUService cpuService: CPUServices){
                Thread cpuThread = new Thread(cpuService);
                cpuThread.start();
            }

            //Conference Threads
            for(ConferenceService conferenceService: conferenceServices){
                Thread conferenceThread = new Thread(conferenceService);
                conferenceThread.start();
            }

            //Student Threads
            for(StudentService studentService: studentServices){
                Thread studentThread = new Thread(studentService);
                studentThread.start();
            }

            //TickService Thread
            timeService.start();



        }catch (FileNotFoundException e){}

    }
}
