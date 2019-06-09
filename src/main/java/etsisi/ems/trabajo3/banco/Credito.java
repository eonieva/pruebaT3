package etsisi.ems.trabajo3.banco;

import java.util.Date;
import java.util.Vector;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.HashMap;

public class Credito extends Tarjeta {
	
	public enum marcas {MASTERCARD, MAESTRO, CLASICA, ELECTRON, OTRA}
	public enum tipos {ORO, PLATINO, CLASICA, OTRA}
	private static final Map<marcas,Double> tarifa = new HashMap<marcas,Double>();
	private static final Map<tipos,Double> credito = new HashMap<tipos,Double>();
	static {
		tarifa.put(marcas.MASTERCARD, 0.05);
		tarifa.put(marcas.MAESTRO, 0.05);
		tarifa.put(marcas.CLASICA, 0.03);
		tarifa.put(marcas.ELECTRON, 0.02);
		tarifa.put(marcas.OTRA, 0.05);
		credito.put(tipos.ORO, 1000.0);
		credito.put(tipos.PLATINO, 800.0);
		credito.put(tipos.CLASICA, 600.0);
		credito.put(tipos.OTRA, 600.0);
	}
	private final int COMISIONMINIMA = 3;
	protected double mCredito;
	public String mNombreEntidad;
	public int mCCV;
	public marcas mMarcaInternacional; //mastercard, maestro, visa ...
	public tipos mTipo; //oro platino clásica
	protected Vector<Movimiento> mMovimientosTarjeta;

	public Credito (String numero, String titular, LocalDate fechacaducidad) {
		super(numero,titular,fechacaducidad);
		this.mMovimientosTarjeta = new Vector<Movimiento>();
	}

	public static BuilderCredito builder(String numero, String titular, LocalDate fechacaducidad) {
		return new BuilderCredito(numero,titular,fechacaducidad);
	}
	
	public void setCuenta(Cuenta c) {
		mCuentaAsociada = c;
	}
	
	public void retirar(double x) throws Exception {	
		double comisiontarifa = tarifa.get(mMarcaInternacional);
		
		double comision = (x * comisiontarifa < COMISIONMINIMA ? COMISIONMINIMA : x * comisiontarifa); 		
		if (x > getCreditoDisponible())
			throw new Exception("Crédito insuficiente");
		
		this.mMovimientosTarjeta.addElement(new Movimiento("Retirada en cuenta asociada (cajero automático)", x + comision));
	}

	//traspaso tarjeta a cuenta
	public void ingresar(double x) throws Exception {
		double comision = (x * 0.05 < COMISIONMINIMA ? COMISIONMINIMA : x * 0.05); // Añadimos una comisión de un 5%, mínimo de 3 euros.		
		if (x > getCreditoDisponible())
			throw new Exception("Crédito insuficiente");
		this.mMovimientosTarjeta.addElement(new Movimiento("Traspaso desde tarjeta a cuenta",x));
		this.mCuentaAsociada.ingresar("Traspaso desde tarjeta a cuenta",x);
		this.mCuentaAsociada.retirar("Comision Traspaso desde tarjeta a cuenta",comision);
	}

	public void pagoEnEstablecimiento(String datos, double x) throws Exception {
		this.mMovimientosTarjeta.addElement(new Movimiento("Compra a crédito en: " + datos, x));
	}

	
	public double getSaldo() {
		double r = 0.0;
		for (int i = 0; i < this.mMovimientosTarjeta.size(); i++) {
			Movimiento m = (Movimiento) mMovimientosTarjeta.elementAt(i);
			r += m.getImporte();			
		}
		return r;
	}
	
	public double getCreditoDisponible() {
		return mCredito - getSaldo();
	}

	public void liquidar(int mes, int anyo) throws Exception {
		double r = 0.0;
		for (int i = 0; i < this.mMovimientosTarjeta.size(); i++) {
			Movimiento m = (Movimiento) this.mMovimientosTarjeta.elementAt(i);
			if (m.getFecha().getMonthValue() == mes && m.getFecha().getYear() == anyo && !m.isLiquidado())
				r += m.getImporte();
				m.setLiquidado(true);
		}
		
		if (r != 0) {
			this.mCuentaAsociada.realizarMovimiento("Liquidación de operaciones tarj. crédito, " + (mes) + " de " + (anyo), -r);
		}
	}
	
	//liquidación parcial sobre el total de los gastos realizados con esa tarjeta durante el mes/año  de liquidación que consiste en lo siguiente: 
	//los gastos totales, incluida una comisión de 12%, se dividen en 3 cuotas a pagar en los 3 meses siguientes 
	public void liquidarPlazos (int mes, int anyo) throws Exception {
		//TODO
	}
	
	public static class BuilderCredito{
		private Credito tarjetaCredito;
		
		public BuilderCredito(String numero, String titular, LocalDate fechacaducidad) {
			this.tarjetaCredito = new Credito (numero, titular, fechacaducidad);
		}

		public BuilderCredito mCredito (double credito) {
			this.tarjetaCredito.mCredito = credito;
			return this;
		}
		public BuilderCredito mNombreEntidad (String nombreentidad) {
			this.tarjetaCredito.mNombreEntidad = nombreentidad;
			return this;
		}
		public BuilderCredito mCCV (int ccv) {
			this.tarjetaCredito.mCCV = ccv;
			return this;
		}
		public BuilderCredito mMarcaInternacional (marcas marcainternacional) {
			this.tarjetaCredito.mMarcaInternacional = marcainternacional;
			return this;
		}
		public BuilderCredito mTipo (tipos tipo) {
			this.tarjetaCredito.mTipo = tipo;
			return this;
		}

		public Credito build() {
			return this.tarjetaCredito;
		}
	}
	
}