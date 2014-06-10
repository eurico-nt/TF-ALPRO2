package roleta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import supermercado.Acumulador;
import supermercado.Caixa;
import supermercado.Cliente;
import supermercado.GeradorClientes;
import supermercado.QueueLinked;
import supermercado.QueueTAD;

/**
 * Classe com a logica da simulacao passo-a-passo da roleta do estádio
 * 
 * @author Daniel Lopes
 * @author Eurico Neto
 */
public class SimulacaoRoleta
{
    private int duracao;
    private double probabilidadeChegada;
    private QueueTAD<Cliente> fila;
    private Caixa caixa;
    private GeradorClientes geradorClientes;
    private Acumulador statTemposEsperaFila;
    private Acumulador statComprimentosFila;
    private boolean trace; //valor indica se a simulacao ira imprimir passo-a-passo os resultados
    
    public SimulacaoRoleta(boolean t) throws FileNotFoundException, IOException
    {
    	Properties dadoSim = new Properties();
    	dadoSim.load(new FileInputStream("dadosSimulacao.properties"));
    	duracao = Integer.parseInt(dadoSim.getProperty("duracao"));
    	probabilidadeChegada = Double.parseDouble(dadoSim.getProperty("probabilidadeChegada"));
    	
        fila = new QueueLinked<Cliente>();
        caixa = new Caixa();
        geradorClientes = new GeradorClientes(probabilidadeChegada);
        statTemposEsperaFila = new Acumulador();
        statComprimentosFila = new Acumulador();
        trace = t;
    }
    
    public void simular()
    {
        //realizar a simulacao por um certo numero de passos de duracao
        for(int tempo=0; tempo<duracao; tempo++)
        {
            //verificar se um cliente chegou
            if(geradorClientes.gerar())
            {
                //se cliente chegou, criar um cliente e inserir na fila do caixa
                Cliente c = new Cliente(geradorClientes.getQuantidadeGerada(),tempo);
                fila.add(c);
                if(trace)
                    System.out.println(tempo + ": cliente " + c.getNumero() + " ("+c.getTempoAtendimento()+" min) entra na fila - " + fila.size() + " pessoa(s)");
            }
            //verificar se o caixa esta vazio
            if(caixa.estaVazio())
            {
                //se o caixa esta vazio, atender o primeiro cliente da fila se ele existir
                if(!fila.isEmpty())
                {
                    //tirar o cliente do inicio da fila e atender no caixa
                    caixa.atenderNovoCliente(fila.remove());
                    statTemposEsperaFila.adicionar(tempo - caixa.getClienteAtual().getInstanteChegada());
                    if(trace)
                        System.out.println(tempo + ": cliente " + caixa.getClienteAtual().getNumero() + " chega ao caixa.");
                }
            }
            else
            {
                //se o caixa ja esta ocupado, diminuir de um em um o tempo de atendimento ate chegar a zero
                if(caixa.getClienteAtual().getTempoAtendimento() == 0)
                {
                    if(trace)
                        System.out.println(tempo + ": cliente " + caixa.getClienteAtual().getNumero() + " deixa o caixa.");
                    caixa.dispensarClienteAtual();
                }
                else
                {
                    caixa.getClienteAtual().decrementarTempoAtendimento();
                }
            }
            statComprimentosFila.adicionar(fila.size());
        }
    }
    
    public void limpar()
    {
        fila = new QueueLinked<Cliente>();
        caixa = new Caixa();
        geradorClientes = new GeradorClientes(probabilidadeChegada);
        statTemposEsperaFila = new Acumulador();
        statComprimentosFila = new Acumulador();
    }
    
    public void imprimirResultados()
    {
        System.out.println();
        System.out.println("Resultados da Simulacao da Roleta");
        System.out.println("Duracao:" + duracao);
        System.out.println("Probabilidade de chegada de clientes:" + probabilidadeChegada);
        System.out.println("Tempo de atendimento minimo:" + Cliente.tempoMinAtendimento);
        System.out.println("Tempo de atendimento maximo:" + Cliente.tempoMaxAtendimento);
        System.out.println("Cliente atendidos:" + caixa.getNumeroAtendidos());
        System.out.println("Clientes ainda na fila:" + fila.size());
        System.out.println("Cliente ainda no caixa:" + (caixa.getClienteAtual() != null));
        System.out.println("Total de clientes gerados:" + geradorClientes.getQuantidadeGerada());
        System.out.println("Tempo medio de espera:" + statTemposEsperaFila.getMedia());
        System.out.println("Comprimento medio da fila:" + statComprimentosFila.getMedia());
    }
}
