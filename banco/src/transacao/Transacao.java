package transacao;

import agencia.Agencia;
import conta.Conta;
import interfaceUsuario.dados.DadosTransacao;
import utilsBank.GeracaoAleatoria;
import utilsBank.databank.Data;
import utilsBank.databank.DataBank;

public class Transacao {
	private Double valor;
	private String nossoNumero;
	private String idPagamento;
	private Data dataEmissaoTransacao;
	private Conta cobrador; //@Lembrando Cobrador cobrara o dinheiro de alguem recebe o dinheiro
	private Conta pagador; //@Lembrando Pagador pagara o dinheiro cobrado pelo cobrador LEMBRANDO QUE SE O PAGADOR FOR NULO DEVE SER UM BOLETO O TIPO DE TRANSACAO
	private Data dataAgendada;
	private Pagavel tipoDeTransacao;

	public Transacao(DadosTransacao dadosTransacao) {
		this.valor = dadosTransacao.getValor();
		this.nossoNumero = GeracaoAleatoria.gerarNossosNumeros(25);
		this.dataEmissaoTransacao = DataBank.criarData(DataBank.COM_HORA);
		this.idPagamento = Agencia.ID_AGENCIA + Agencia.CODIGO_MOEDA + GeracaoAleatoria.gerarNumeros(4) +
				this.nossoNumero + dadosTransacao.getDataVencimentoString() + dataEmissaoTransacao.toString(DataBank.SEM_HORA);
		this.cobrador = dadosTransacao.getCobrador();
		this.pagador = dadosTransacao.getPagador();
		this.tipoDeTransacao = dadosTransacao.getTipoDaTransacao();
	}

	public Transacao(DadosTransacao dadosTransacao, Data dataAgendada) {
		Transacao transacao = new Transacao(dadosTransacao);
		transacao.dataAgendada = dataAgendada;
	}

	public void gerarComprovante() {
		// TODO: 6/5/2022  Perguntar a vania se eh melhor o modulo da interfaace gerar um comprovante ou se a transacao eh responsavel....
		//  e perguntar se criar uma classe comprovante seria bom ou so fazer um ToString de Transacao
	}

	public Data getDataAgendada() {
		return dataAgendada;
	}

	public Conta getPagador() {
		return pagador;
	}

	public Double getValor() {
		return valor;
	}

	public Conta getCobrador() {
		return cobrador;
	}
}
