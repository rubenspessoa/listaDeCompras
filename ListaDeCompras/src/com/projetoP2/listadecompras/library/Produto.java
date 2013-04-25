package com.projetoP2.listadecompras.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * Classe que cria e edita um produto.
 * @author Arthur Felipe, Joao Paulo Ribeiro, Rubens Pessoa, Victor Souto
 *
 */
public abstract class Produto implements Serializable, Calculavel, Comparable<Produto> {
    
        /**
         * 
         */
        private static final long serialVersionUID = -2017858648084823895L;
       
        private static final int dia = 86400000;
        private String nome, estabelecimento;
		private double valor;
        private LinkedList<EventoDePreco> eventosDePreco = new LinkedList<EventoDePreco>();
        private ArrayList<String> palavrasChave = new ArrayList<String>();
 
        public Produto (String nome, String estabelecimento, double valor) {
                this.nome = nome;
                this.estabelecimento = estabelecimento;
                this.valor = valor;
                String[] criaPalavrasChave = nome.split(" ");
                for ( int i = 0; i < criaPalavrasChave.length; i++) {
                		if (!criaPalavrasChave.equals(""))
                			palavrasChave.add(criaPalavrasChave[i]);
                }
        }
        
        /**
         * Adiciona um evento de preco ao produto
         * @param valor do produto
         * @param estabelecimento local onde foi encontrado
         */
        
		public abstract void addEventoDePreco(double quantidade, double valor, String estabelecimento);
		
		public void addPalavrasChave(String palavra){
			String[] teste = palavra.trim().split(",");
			for (int i = 0; i < teste.length; i++) {
				addPalavraChave(teste[i]);
			}
		}
		
		private void addPalavraChave(String palavra) {
			palavrasChave.add(palavra);
		}
		
		public ArrayList<String> getPalavrasChave(){
			return palavrasChave;
		}
		
		public String getNome() {
			return nome;
		}
		
        public String getEstabelecimento() {
			return estabelecimento;
		}
		
        public double getValor() {
        		return valor;
        }
        
        public LinkedList<EventoDePreco> getEventosDePreco() {
			return eventosDePreco;
		}

		public void setEventosDePreco(LinkedList<EventoDePreco> eventosDePreco) {
			this.eventosDePreco = eventosDePreco;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public void setEstabelecimento(String estabelecimento) {
			this.estabelecimento = estabelecimento;
		}

		public void setValor(double valor) {
			this.valor = valor;
		}

		public void setPalavrasChave(ArrayList<String> palavrasChave) {
			this.palavrasChave = palavrasChave;
		}
        
        public int quantasVezesFoiComprado(){
        		return eventosDePreco.size();
        }
		
        /**
		 * Retorna uma lista de eventos de pre�o referentes � um intervalo de tempo terminando na presente data. 
		 * O tamanho do intervalo � medido em dias e a ordem da lista retornada �: eventos mais recentes primeiro.
		 * 
		 * @return LinkedList<EventoDePreco> amostraEventosDePreco
		 */
		
		public LinkedList<EventoDePreco> getAmostraEventosDePreco(){
			int tamanhoIntervalo = 90; //Tamanho do intervalo em dias. Importante: esse valor � um par�metro suscet�vel a mudan�as para calibrar a sugest�o.
			Date dataFim = new Date();
			long dataInicioArredondada = (((dataFim.getTime()/dia) - tamanhoIntervalo)*dia);
			LinkedList<EventoDePreco> amostraEventosDePreco = new LinkedList<EventoDePreco>();
			for(int i = this.eventosDePreco.size()-1 ; i >= 0; i--){
				if (this.eventosDePreco.get(i).getData().getTime()>dataInicioArredondada){
					amostraEventosDePreco.addFirst(this.eventosDePreco.get(i));
				}else{
					break;
				}
			}
			return amostraEventosDePreco;
		}
		
		/**
		 * Gera uma array de intervalos de compras - em dias. � usado em getTendenciaIntervalo
		 * @return int [] ArrayIntervalos
		 */
		
		private int[] getIntervalosDeCompra(){
			int sizeEventosDePreco = getAmostraEventosDePreco().size();
			if ( sizeEventosDePreco < 2){

				int[] ArrayIntervalos = new int[1];
				ArrayIntervalos[0] = 15; //Valor arbitr�rio para o caso de  n�o haver eventos de compra suficientes.

				return ArrayIntervalos;
			}
			if ( sizeEventosDePreco == 2){

				int[] ArrayIntervalos = new int[1];
				int evento_corrente = (int) (getAmostraEventosDePreco().get(0).getData().getTime()/dia);
				int evento_proximo = (int)(getAmostraEventosDePreco().get(1).getData().getTime()/dia);
				ArrayIntervalos[0] = evento_proximo - evento_corrente;

				return ArrayIntervalos;
				
			}else{

				int[] ArrayIntervalos = new int[getAmostraEventosDePreco().size() - 1];
				for(int i = 0 ; i == getAmostraEventosDePreco().size() -2 ; i++){
					int evento_corrente = (int)(getAmostraEventosDePreco().get(i).getData().getTime()/dia);
					int evento_proximo =  (int)(getAmostraEventosDePreco().get(i+1).getData().getTime()/dia);
					ArrayIntervalos[i] = evento_proximo - evento_corrente;
				}

				return ArrayIntervalos;
			}
		}
		
		/**
		 * 	Retorna o intervalo de compras mais frequente - em dias - para 
		 * este produto a partir de uma medida de tend�ncia central ( Atualmente utilizando a moda.)
		 * Chama o m�todo getIntervalosDeCompra para iterar sobre
		 * 
		 * @return int moda
		 */
		
		private int getTendenciaDeIntervalo(){
			int moda = 0;
			int[] a = getIntervalosDeCompra();
			if (a.length == 1){
				return a[0];
			}
			int maxcount = 0;
			for (int i : a) {
		        int count = 0;
		        for (int j : a) {
		            if (a[j] == a[i]){
		            	count+=1;}
		        }
		        if (count >= maxcount) {
		            moda = a[i];
		            maxcount = count;
		        }
		    }
			return moda;
			
		}
		/**
		 * M�todo que retorna um �ndice usado como tend�ncia de compra. Este �ndice � o n�mero de dias que faltam
		 * para a data prov�vel para a pr�xima compra do produto, de acordo com os eventos de pre�o dele.
		 * Quanto mais pr�ximo de 0, mais prov�vel � que esse produto tenha que ser comprado na data atual.
		 */
		
		public int getTendenciaDeCompra(){
			Date dataHoje = new Date();
			int diaHoje = (int)dataHoje.getTime()/dia;
			int tendencia = diaHoje - ((int)(getAmostraEventosDePreco().getLast().getData().getTime()/dia) + getTendenciaDeIntervalo()); 
			if (tendencia < 0){
				return -tendencia;
			}
			return tendencia;
		}
		
		public EventoDePreco melhorEventoDePreco(){
			EventoDePreco melhorEvento = eventosDePreco.getFirst();
			double melhorPreco = eventosDePreco.getFirst().getValorPago();
			for (EventoDePreco evento : eventosDePreco) {
				if (evento.getValorPago() < melhorPreco){
					melhorEvento = evento;
					melhorPreco = evento.getValorPago();
				}
			}
			
			return melhorEvento;
		}
}