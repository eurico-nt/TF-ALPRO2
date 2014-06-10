package supermercado;

import java.io.IOException;

/**
 * Programa principal da simulacao da roleta do estádio
 * 
 * @author Daniel Lopes
 * @author Eurico Neto
 */
public class SimuladorRoleta
{
    public static void main(String[] args) throws IOException
    {
        SimulacaoRoleta sim = new SimulacaoRoleta(true);
        sim.simular();
        sim.imprimirResultados();
    }
}
