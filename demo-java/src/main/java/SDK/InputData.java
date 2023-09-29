package SDK;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class InputData {
    private static Logger logJava = Logger.getLogger(InputData.class);

    public String token;
    public String projectId;
    public String inboxId;
    public JSONArray tasks;
    public JSONObject inputs;
    public JSONArray outputs;

    public InputData(String token, String projectId, String inboxId, JSONArray tasks, JSONObject inputs, JSONArray outputs) {
        this.token = token;
        this.projectId = projectId;
        this.inboxId = inboxId;
        this.tasks = tasks;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getInboxId() {
        return inboxId;
    }

    public void setInboxId(String inboxId) {
        this.inboxId = inboxId;
    }

    public JSONArray getTasks() {
        return tasks;
    }

    public void setTasks(JSONArray tasks) {
        this.tasks = tasks;
    }

    public JSONObject getInputs() {
        return inputs;
    }

    public void setInputs(JSONObject inputs) {
        this.inputs = inputs;
    }

    public JSONArray getOutputs() {
        return outputs;
    }

    public void setOutputs(JSONArray outputs) {
        this.outputs = outputs;
    }

    public static void setDataInputs(JSONObject data, InputData inputData) throws Exception{
        String token;
        String projectId;
        String inboxId;
        JSONArray tasks;
        JSONObject inputs;
        JSONArray outputs;

        try{
            logJava.info("Extraer datos de entrada");

            try {
                token = data.getJSONObject("data").getString("token");
                if ("".equals(token)) {
                    logJava.error("Contenido de token esta vacio");
                    throw new Exception("El contenido de token esta vacio");
                }
                inputData.setToken(token);
            } catch (Exception e) {
                logJava.error("No se encontro token o es invalido");
                throw new Exception("No se encontro token o es invalido");
            }

            try {
                projectId = data.getJSONObject("data").getString("projectId");
                if ("".equals(projectId)) {
                    logJava.error("El contenido de projectId esta vacio");
                    throw new Exception("El contenido de projectId esta vacio");
                }
                inputData.setProjectId(projectId);
            } catch (Exception e) {
                logJava.error("No se encontro projectId o es invalido");
                throw new Exception("No se encontro projectId o es invalido");
            }

            //Obtener dato "inboxId" del objeto JSON "data"
            try {
                inboxId = data.getJSONObject("data").getString("inboxId");
                if ("".equals(inboxId)) {
                    logJava.error("El contenido de inboxId esta vacio");
                    throw new Exception("El contenido de inboxId esta vacio");
                }
                inputData.setInboxId(inboxId);
            } catch (Exception e) {
                logJava.error("No se enoontro inboxId o es invalido");
                throw new Exception("No se encontro inboxId o es invalido");
            }

            //Obtener arreglo "tasks" del objeto JSON "data"
            try {
                tasks = data.getJSONObject("data").getJSONArray("tasks");
                if ("".equals(tasks)) {
                    logJava.error("El contenido de task esta vacio");
                    throw new Exception("El contenido de tasks esta vacio");
                }
                inputData.setTasks(tasks);
            } catch (Exception e) {
                logJava.error("No se encontraron tareas a realizar o su contenido no es valido");
                throw new Exception("No se encontraron tareas a realizar o su contenido no es valido");
            }

            //Obtener objeto JSON "inputs" del objeto JSON "data"
            try {
                inputs = data.getJSONObject("data").getJSONObject("inputs");
                if (inputs.toString().equals("{}")) {
                    logJava.error("El contenido de inputs esta vacio");
                    throw new Exception("El contenido de inputs esta vacio");
                }
                inputData.setInputs(inputs);
            } catch (Exception e) {
                logJava.error("No se encontraron inputs o su contenido no es valido");
                throw new Exception("No se encontraron inputs o es invalido");
            }

            //Obtener arreglo "outputs" del objeto JSON "data"
            try {
                outputs = data.getJSONObject("data").getJSONArray("outputs");
                if (outputs.toString().equals("{}")) {
                    logJava.error("El contenido de outputs esta vacio");
                    throw new Exception("El contenido de outputs esta vacio");
                }
                inputData.setOutputs(outputs);
            } catch (Exception e) {
                logJava.error("No se encontraron outputs o su contenido no es valido");
                throw new Exception("No se encontraron outputs o es invalido");
            }

        }catch (Exception e){
            throw e;
        }

    }
}
