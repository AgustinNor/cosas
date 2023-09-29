package SDK;

public class PathFiles {
    private  static int count = 0;
    private static PathFiles[] instances = new PathFiles[0];

    String[] pathArray;
    String dataType;

    public PathFiles(String[] pathArray, String dataType) {
        this.pathArray = pathArray;
        this.dataType = dataType;

        if (count >= instances.length) {
            // Si el contador excede el tamaño del array, redimensiona el array
            PathFiles[] intancesCount = new PathFiles[instances.length + 2];
            System.arraycopy(instances, 0, intancesCount, 0, instances.length);
            instances = intancesCount;
        }

        instances[count++] = this; // Agregar la instancia al array y luego incrementar el contador
    }

    public String[] getPathArray() {
        return pathArray;
    }

    public void setPathArray(String[] pathArray) {
        this.pathArray = pathArray;
    }

    public String getDataType() {
        return dataType;
    }

    public static PathFiles[] obtenerInstancias() {
        PathFiles[] resultado = new PathFiles[count];
        System.arraycopy(instances, 0, resultado, 0, count);
        return resultado;
    }
}
