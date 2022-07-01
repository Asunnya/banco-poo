package interfaceUsuario;

import agencia.Agencia;
import agencia.exceptions.InsercaoException;
import cliente.Cliente;
import cliente.ClienteEmpresa;
import cliente.ClientePessoa;
import cliente.Endereco;
import cliente.exceptions.LoginException;
import conta.exceptions.TipoInvalido;
import interfaceUsuario.Exceptions.ValorInvalido;
import interfaceUsuario.dados.DadosCartao;
import interfaceUsuario.dados.DadosChavesPix;
import interfaceUsuario.dados.DadosConta;
import interfaceUsuario.dados.DadosTransacao;
import utilsBank.GerenciadorBanco;

import java.util.Scanner;

public class MenuUsuario {
	public static final String STANDARD = "standard";
	public static final String PREMIUM = "premium";
	public static final String DIAMOND = "diamond";
	public static final String DEPOSITO = "do seu Deposito";
	public static final String TRANSFERENCIA = "da sua transferencia";
	public static final String PAGAMENTO = "do seu pagamento";
	public static final String FORMATO_DATAS = "31/12/2002";
	public static final String BOLETO = "boleto";
	public static final String PIX = "pix";
	public static final String CHAVES_DISPONIVEIS = "chave_aleatoria | telefone | email | identificacao (CPF OU CNPJ)";
	private static final Scanner teclado = new Scanner(System.in);
	private static final double RENDA_MINIMA = 200.0;
	private static final double RENDA_MAXIMA_STANDARD = 10000.0;
	private static final double RENDA_MAXIMA_PREMIUM = 30000.0;
	private static final double RENDA_MINIMA_DIAMOND = 30001.0;

	public static void iniciar() {
		boolean loop = true;
		while (loop) {
			imprimirBorda("=", 30);
			System.out.println("[0] - Encerrar programa");
			System.out.println("[1] - Acessar conta");
			System.out.println("[2] - Criar conta");
			imprimirBorda("=", 30);
			System.out.print("\n> ");
			try {
				switch (teclado.nextLine()) {
					case "0":
						loop = false;
						break;
					case "1":
						//Login
						logar();
						break;
					case "2":
						InterfaceUsuario.setClienteAtual(criarCliente());
						menuCriacaoConta();
						break;
					default:
						//Opção inválida
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public static void menuCliente() {
		boolean loop = true;
		Cliente cliente = InterfaceUsuario.getClienteAtual();
		if (cliente != null) {

			while (loop) {
				imprimirBorda("=", 30);
				System.out.println("Bem vindo " + cliente.getNome());
				System.out.println("[0] - Sair");
				System.out.println("[1] - Transferir");
				System.out.println("[2] - Pagar");
				System.out.println("[3] - Depositar");
				System.out.println("[4] - Agendar transferencia");
				System.out.println("[5] - Pagar fatura");
				System.out.println("[6] - Criar chave Pix");
				System.out.println("[7] - Modificar chave Pix");
				imprimirBorda("=", 30);
				System.out.print("\n> ");
				try {
					String value = teclado.nextLine();
					switch (value) {
						case "0":
							System.out.println("Obrigada por acessar ao nosso Internet Banking!");
							loop = false;
							break;
						case "1":
							MenuDadosTransacao(TRANSFERENCIA);
							cliente.getConta().transferir();
							break;
						case "2":
							MenuDadosTransacao(PAGAMENTO);
							cliente.getConta().pagar();
							break;
						case "3":
							MenuDadosTransacao(DEPOSITO);
							cliente.getConta().depositar();
							break;
						default:
							//Opção inválida
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		}
	}

	public static void MenuDadosTransacao(String tipoOperacao) {
		imprimirBorda("-", 20);
		boolean debitoAutomatico = false;
		String[] cabecalhoDadosTransacao = {
				"Digite o valor " + tipoOperacao,
				"Digite o tipo de Transacao [FORMATO : " + MenuUsuario.BOLETO + " " + MenuUsuario.PIX + " ]+",
		};

		String[] entradaDadosTransacao = new String[cabecalhoDadosTransacao.length];

		for (int i = 0; i < cabecalhoDadosTransacao.length; i++) {
			System.out.printf("%s:\n> ", cabecalhoDadosTransacao[i]);
			entradaDadosTransacao[i] = teclado.nextLine();
		}
		verificarValorTransacao(entradaDadosTransacao[0]);

		if (entradaDadosTransacao[1].equals(MenuUsuario.BOLETO)) {
			String[] cabecalhoDadosBoleto = {
					"Digite o dia de vencimento do boleto " + FORMATO_DATAS + " ]",
					"Digite o valor da multa por dias do boleto",
			};
			String[] entradaDadosBoleto = new String[cabecalhoDadosBoleto.length];
			for (int i = 0; i < cabecalhoDadosBoleto.length; i++) {
				System.out.printf("%s:\n> ", cabecalhoDadosBoleto[i]);
				entradaDadosBoleto[i] = teclado.nextLine();
			}

			InterfaceUsuario.setDadosTransacao(new DadosTransacao(
					Double.parseDouble(entradaDadosTransacao[0]),
					entradaDadosBoleto[0],
					Integer.parseInt(entradaDadosBoleto[1]),
					MenuUsuario.BOLETO,
					InterfaceUsuario.getClienteAtual().getIdentificacao())
			);
		} else if (entradaDadosTransacao[1].equals(MenuUsuario.PIX)) {
			String[] cabecalhoDadosPix = {
					"Digite o tipo da chave inserida [FORMATOS DISPONIVEIS]: " + CHAVES_DISPONIVEIS,
					"Digite a chave",
			};
			String[] entradaDadosPix = new String[cabecalhoDadosPix.length];
			for (int i = 0; i < cabecalhoDadosPix.length; i++) {
				System.out.printf("%s:\n> ", cabecalhoDadosPix[i]);
				entradaDadosPix[i] = teclado.nextLine();
			}

			InterfaceUsuario.setDadosTransacao(new DadosTransacao(
					Double.parseDouble(entradaDadosTransacao[0]),
					MenuUsuario.PIX,
					InterfaceUsuario.getClienteAtual().getIdentificacao(),
					entradaDadosPix[1],
					DadosChavesPix.IDENTIFICACAO,
					entradaDadosPix[0]
			));
		}

		InterfaceUsuario.getClienteAtual().criarConta();


	}

	private static void imprimirBorda(String padrao, int tam) {
		for (int i = 0; i < tam; i++) {
			System.out.print(padrao);
		}
		System.out.println();
	}

	private static Cliente criarCliente() throws InsercaoException {
		imprimirBorda("-", 20);
		System.out.print("""
				Tipo de cliente:
				[0] - Cancelar
				[1] - Pessoa fisica
				[2] - Pessoa juridica
				>\s
				""");
		String tipo = teclado.nextLine();

		String tag = (tipo.equals("1")) ? "CPF" : "CNPJ";

		String[] cabecalhoEndereco = {
				"CEP",
				"Numero da Residencia",
				"Complemento (Opcional)",
		};
		String[] cabecalhoGeral = {
				"Nome completo",
				"Email",
				"Telefone",
				"Idade",
				"Renda",
				tag,
				"Senha",
		};

		String[] entradaEndereco = new String[cabecalhoEndereco.length];
		for (int i = 0; i < cabecalhoEndereco.length; i++) {
			System.out.printf("%s:\n> ", cabecalhoEndereco[i]);
			entradaEndereco[i] = teclado.nextLine();
		}
		Endereco endereco = new Endereco(
				Integer.parseInt(entradaEndereco[0]),
				Integer.parseInt(entradaEndereco[1]),
				entradaEndereco[2]
		);

		String[] entradaGeral = new String[cabecalhoGeral.length];
		for (int i = 0; i < cabecalhoGeral.length; i++) {
			System.out.printf("%s:\n> ", cabecalhoGeral[i]);
			entradaGeral[i] = teclado.nextLine();
		}

		Cliente cliente;
		if (tipo.equalsIgnoreCase("1")) {
			cliente = new ClientePessoa(
					entradaGeral[0],
					entradaGeral[1],
					entradaGeral[2],
					Integer.parseInt(entradaGeral[3]),
					endereco,
					Double.parseDouble(entradaGeral[4]),
					entradaGeral[5],
					entradaGeral[6]
			);
		} else {
			cliente = new ClienteEmpresa(
					entradaGeral[0],
					entradaGeral[1],
					entradaGeral[2],
					Integer.parseInt(entradaGeral[3]),
					endereco,
					Double.parseDouble(entradaGeral[4]),
					entradaGeral[5],
					entradaGeral[6]
			);
		}
		Agencia.getInstance().addCliente(cliente);
		return cliente;
	}

	private static void menuCriacaoConta() {
		imprimirBorda("-", 20);
		Double renda = inserirRenda();
		boolean debitoAutomatico = false;
		String[] cabecalhoCartoesGeral = {
				"Digite um apelido para seu novo cartao",
				"Deseja Cartao de Credito? [1] SIM [0] NAO",
				"Deseja Cartao de Debito? [1] SIM [0] NAO",
		};

		String[] entradaCartoesGeral = new String[cabecalhoCartoesGeral.length];
		for (int i = 0; i < cabecalhoCartoesGeral.length; i++) {
			System.out.printf("%s:\n> ", cabecalhoCartoesGeral[i]);
			entradaCartoesGeral[i] = teclado.nextLine();
		}
		if (Integer.parseInt(entradaCartoesGeral[1]) == 0) {
			System.out.println("Deseja debito automatico? [1] SIM [0] NAO");
			debitoAutomatico = GerenciadorBanco.intToBoolean(Integer.parseInt(teclado.nextLine()));
		}
		InterfaceUsuario.setDadosConta(new DadosConta(
				tipoDeContaPelaRenda(renda),
				renda,
				GerenciadorBanco.intToBoolean(Integer.parseInt(entradaCartoesGeral[0])),
				GerenciadorBanco.intToBoolean(Integer.parseInt(entradaCartoesGeral[1])),
				debitoAutomatico)
		);
		InterfaceUsuario.setDadosCartao(new DadosCartao(
				entradaCartoesGeral[0],
				entradaCartoesGeral[1],
				entradaCartoesGeral[2]
		));
		InterfaceUsuario.getClienteAtual().criarConta();
	}

	private static String tipoDeContaPelaRenda(Double renda) {
		if (renda <= RENDA_MAXIMA_STANDARD) {
			return STANDARD;
		} else if (renda <= RENDA_MAXIMA_PREMIUM) {
			return PREMIUM;
		} else if (renda > RENDA_MAXIMA_PREMIUM) {
			return DIAMOND;
		}
		throw new TipoInvalido("O tipo de conta nao pode ser definido");
	}

	private static void verificarValorTransacao(String value) {
		//TODO verificar o valor com try exceptions etc
	}

	private static void verificarRenda(Double renda) throws ValorInvalido {
		if (0.0 < renda) {
			throw new ValorInvalido("[ERRO] Valor negativo para sua renda");
		} else if (renda < RENDA_MINIMA) {
			throw new ValorInvalido("[RENDA MINIMA NAO PERMITIDA] As regras da Agencia nao permite essa renda, por favor, consulte nossos termos de uso!");
		}
	}

	private static Double inserirRenda() {
		double renda = 0.0;
		while (renda < RENDA_MINIMA) {
			try {
				System.out.println("Por favor, Insira sua Renda");
				renda = Double.parseDouble(teclado.nextLine());
				verificarRenda(renda);
			} catch (ValorInvalido ex) {
				System.out.println(ex.getMessage());
			}
		}
		return renda;
	}

	private static <T extends Cliente> void logar() throws LoginException {
		String[] cabecalho = {
				"CPF/CNPJ",
				"Senha",
		};

		String[] entrada = new String[cabecalho.length];
		for (int i = 0; i < cabecalho.length; i++) {
			System.out.printf("%s:\n> ", cabecalho[i]);
			entrada[i] = teclado.nextLine();
		}

		Cliente cliente = Agencia.buscarCliente(entrada[0]);
		if (cliente != null) {
			cliente.verificarSenha(entrada[1]);
			InterfaceUsuario.setClienteAtual(cliente);
			System.out.println("Login realizado com sucesso");
		} else {
			throw new LoginException("Cliente nao encontrado");
		}
	}
}
