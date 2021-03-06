package ex3;

// Original source code: https://gist.github.com/amadamala/3cdd53cb5a6b1c1df540981ab0245479
// Modified by Fernando Porrino Serrano for academic purposes.

import java.util.ArrayList;

public class HashTable {
    private int INITIAL_SIZE = 16;
    private int size ;
    private HashEntry[] entries = new HashEntry[INITIAL_SIZE];
    private HashEntry[] tempEntries = new HashEntry[INITIAL_SIZE];


    /**
     * Error: Devolvía siempre cero
     * La variable no se incrementaba en ningún lado
     * Solucionado
     * @return Devuelve size
     */
    public int size(){
        return this.size;
    }

    /**
     * Funciona correctamente
     * @return Devuelve Real size
     */
    public int realSize(){
        return this.INITIAL_SIZE;
    }


    /**
     * Fallaba al introducir un elemento con la misma key
     *
     * Solucionado: Ahora lo sobreEscribe
     * @param key key
     * @param value valor
     */
    public void put(String key, Cliente value) {
        boolean yaSobreEscrito = false;
        int hash = getHash(key);
        final HashEntry hashEntry = new HashEntry(key, value);// borra valors prev i next i don valor key, value

        /**
         * SobreEscribe Elemento con la misma key
         */
        yaSobreEscrito = sobreEscribirElemento(key, value, yaSobreEscrito);

        if (!yaSobreEscrito) {

            if (entries[hash] == null) {
                entries[hash] = hashEntry; // AQUI POSA VALOR  key, value A LA POSICIO hash (que es key) si esta buit
                size++;
            } else {
                tempEntries = entries;

                while (entries[hash] != null) {
                    INITIAL_SIZE = INITIAL_SIZE * 2;
                    hash = getHash(key);
                    entries = new HashEntry[INITIAL_SIZE];

                    for (int i = 0; i < tempEntries.length; i++) {
                        entries[i] = tempEntries[i];
                    }
                }
                entries[hash] = hashEntry;
                size++;
            }
        }
    }

    /**
     * Extracción de método
     * Sobre Escribe Elemento con la misma key
     *
     * @param key
     * @param value
     * @param yaSobreEscrito
     * @return
     */
    private boolean sobreEscribirElemento(String key, Cliente value, boolean yaSobreEscrito) {
        for (int i = 0; i < INITIAL_SIZE; i++) {
            if ( entries[i] != null && entries[i].key.equals(key)  ) {
                entries[i].value = value.nombre + " " + value.email;
                yaSobreEscrito = true;
            }
        }
        return yaSobreEscrito;
    }


    /**
     * Returns 'null' if the element is not found.
     *
     * Este método funciona correctamente
     */
    public String get(String key) {
        int hash = getHash(key);
        if(entries[hash] != null) {
            HashEntry temp = entries[hash];

            while( !temp.key.equals(key))
                temp = temp.next;

            return temp.value;
        }

        return null;
    }


    /**
     * Errores encontrados:
     *     -> Si borrabas primer elemento, lo borraba xtodo, colisiones incluidas
     *     -> Si el elemento a borrar tenia un key mayor que colisiones anteriores daba NULl POINTER EXECEPTIONS
     *
     *          TODO solucionado
     */
    public void drop(String key) {
        int hash = getHash(key);
        if(entries[hash] != null) {

            HashTable.HashEntry temp = entries[hash];
            while( (!temp.key.equals(key)) && (temp.next != null)) {

                temp = temp.next;
            }
            /**
             * UFF  con el while anterior verificando el null o el if de acontinuación verificando la key
             * soluciono el tema que cuando borraba un elemento que ya habia sido borrado
             * Sucedia que el elemento siguiente tenia una key superior a la que estaba buscando para eliminar
             * Entonces por mucho next que hiciera, nunca encontraba el elemento que quier borrar y daba Null pointer Exception
             *
             * En el caso de que el elemento que busco para borrar estuviera detras de otros elementos, entonces si que
             * en el while anterior va entrando y haciendo next buscando el elemento, y si no existe no hay problema .
             *
             */
            if (temp.key.equals(key)) { // UFFF verifica key - si la key no existe se lo salta xtodo y no hace nada.
                if (temp.prev == null) {
                    // Extración de método,
                    eliminarPrimerElemento(hash);
                } else {
                    // Extracción de método
                    eliminaColisión(temp);
                }
            }
        }
    }

    /**
     * Extracción de método, se ve más claro con el nombre del método que hace este código
     * @param temp
     */
    private void eliminaColisión(HashEntry temp) {
        if (temp.next != null)
            temp.next.prev = temp.prev;   //esborrem temp, per tant actualitzem l'anterior al següent
        temp.prev.next = temp.next;                         //esborrem temp, per tant actualitzem el següent de l'anterior
    }

    /**
     * Extracción de método, se ve más claro con el nombre del método que hace este código
     * @param hash
     */
    private void eliminarPrimerElemento(int hash) {
        /**
         * Aqui no actuaba correctamente
         * Ahora en caso de ser el primer elemento lo borra dejando los siguientes
         */
        entries[hash] = entries[hash].next;  // AQUI al primer valor le doy el valor del siguiente
        if (entries[hash] != null)
            entries[hash].prev = null; // En caso de no haber siguiente pues hash borrado entero
    }

    /**
     * Obtiene el hash, funciona ok.
     * @param key
     * @return
     */
    private int getHash(String key) {
        // piggy backing on java string
        // hashcode implementation.
        return key.hashCode() % INITIAL_SIZE;
    }

    private class HashEntry {
        String key;
        String value;

        // Linked list of same hash entries.
        HashEntry next;
        HashEntry prev;

        public HashEntry(String key, Cliente value) {
            this.key = key;
            this.value = value.nombre + " " + value.email;
            this.next = null;
            this.prev = null;
        }

        @Override
        public String toString() {
            return "[" + key + ", " + value + "]";
        }
    }

    @Override
    public String toString() {
        int bucket = 0;
        StringBuilder hashTableStr = new StringBuilder();
        for (HashEntry entry : entries) {
            if(entry == null) {
                bucket++;
                continue;
            }
            hashTableStr.append("\n bucket[")
                    .append(bucket)
                    .append("] = ")
                    .append(entry.toString());
            bucket++;
            HashEntry temp = entry.next;
            while(temp != null) {
                hashTableStr.append(" -> ");
                hashTableStr.append(temp.toString());
                temp = temp.next;
            }
        }
        return hashTableStr.toString();
    }

    public ArrayList<String> getCollisionsForKey(String key) {
        return getCollisionsForKey(key, 1);
    }

    public ArrayList<String> getCollisionsForKey(String key, int quantity){
        /*
          Main idea:
          alphabet = {0, 1, 2}

          Step 1: "000"
          Step 2: "001"
          Step 3: "002"
          Step 4: "010"
          Step 5: "011"
           ...
          Step N: "222"

          All those keys will be hashed and checking if collides with the given one.
        * */

        final char[] alphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        ArrayList<Integer> newKey = new ArrayList();
        ArrayList<String> foundKeys = new ArrayList();

        newKey.add(0);
        int collision = getHash(key);
        int current = newKey.size() -1;

        while (foundKeys.size() < quantity){
            //building current key
            String currentKey = "";
            for(int i = 0; i < newKey.size(); i++)
                currentKey += alphabet[newKey.get(i)];

            if(!currentKey.equals(key) && getHash(currentKey) == collision)
                foundKeys.add(currentKey);

            //increasing the current alphabet key
            newKey.set(current, newKey.get(current)+1);

            //overflow over the alphabet on current!
            if(newKey.get(current) == alphabet.length){
                int previous = current;
                do{
                    //increasing the previous to current alphabet key
                    previous--;
                    if(previous >= 0)  newKey.set(previous, newKey.get(previous) + 1);
                }
                while (previous >= 0 && newKey.get(previous) == alphabet.length);

                //cleaning
                for(int i = previous + 1; i < newKey.size(); i++)
                    newKey.set(i, 0);

                //increasing size on underflow over the key size
                if(previous < 0) newKey.add(0);

                current = newKey.size() -1;
            }
        }

        return  foundKeys;
    }
    /**
     * Extracción de Clase con Delegate
     * Lo extraje para separar los métodos del main
     * De esta forma da mas claridad al código ya que el main no cuesta de encontrar
     * Aqui había el main
     *
     */
}