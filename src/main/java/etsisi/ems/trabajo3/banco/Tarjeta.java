package etsisi.ems.trabajo3.banco;

import java.time.LocalDate;

public abstract class Tarjeta {

	protected String mNumero;
	protected String mTitular;
	protected LocalDate mFechaDeCaducidad;
	protected Cuenta mCuentaAsociada;
	
	public Tarjeta(String numero, String titular, LocalDate fecha) {
		this.mNumero = numero;
		this.mTitular = titular;
		this.mFechaDeCaducidad = fecha;
	}
	
	public void setCuenta(Cuenta cuenta) {
		this.mCuentaAsociada = cuenta;
	}
	
	abstract public void retirar(double x) throws Exception;
	abstract public void ingresar(double x) throws Exception;
	abstract public void pagoEnEstablecimiento(String datos, double x) throws Exception;
	abstract public double getSaldo();
}
