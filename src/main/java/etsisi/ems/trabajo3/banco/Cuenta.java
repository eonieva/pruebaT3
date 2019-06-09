package etsisi.ems.trabajo3.banco;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Vector;

public class Cuenta {

    private String mNumero;
    private String mTitular;
    private Vector<Movimiento> mMovimientos;

    public Cuenta(String numero, String titular) {
	this.mNumero = numero;
	this.mTitular = titular;
	this.mMovimientos = new Vector<Movimiento>();
    }

    public void ingresar(double x) throws Exception {
	this.ingresar("SIN CONCEPTO", x);
    }

    public void retirar(double x) throws Exception {
	this.retirar("SIN CONCEPTO", x);
    }

    public void ingresar(String concepto, double x) throws Exception {
	if (x <= 0)
	    throw new Exception("No se puede ingresar una cantidad negativa");
	this.realizarMovimiento(concepto, x);
    }

    public void retirar(String concepto, double x) throws Exception {
	if (x <= 0)
	    throw new Exception("No se puede retirar una cantidad negativa");
	if (getSaldo() < x)
	    throw new Exception("Saldo insuficiente");
	this.realizarMovimiento(concepto, -x);
    }

    public double getSaldo() {
	double r = 0.0;
	for (int i = 0; i < this.mMovimientos.size(); i++) {
	    Movimiento m = (Movimiento) mMovimientos.elementAt(i);
	    r += m.getImporte();
	}
	return r;
    }

    public void realizarMovimiento(String concepto, double x) {
	Movimiento m = new Movimiento();
	m.setConcepto(concepto);
	m.setImporte(x);
	Date date = new Date();
	LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	m.setFecha(fecha);
	this.mMovimientos.addElement(m);
    }

}