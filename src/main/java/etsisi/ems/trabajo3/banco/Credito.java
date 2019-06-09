package etsisi.ems.trabajo3.banco;

import java.time.LocalDate;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class Credito extends Tarjeta {

    public enum marcas {
	MASTERCARD, MAESTRO, CLASICA, ELECTRON, OTRA
    }

    public enum tipos {
	ORO, PLATINO, CLASICA, OTRA
    }

    private final int MINCOMISION = 3;
    private final double BASECOMISION = 0.05;
    private static final Map<marcas, Double> TARIFA = new HashMap<marcas, Double>();
    private static final Map<tipos, Double> CREDITO = new HashMap<tipos, Double>();
    static {
	TARIFA.put(marcas.MASTERCARD, 0.05);
	TARIFA.put(marcas.MAESTRO, 0.05);
	TARIFA.put(marcas.CLASICA, 0.03);
	TARIFA.put(marcas.ELECTRON, 0.02);
	TARIFA.put(marcas.OTRA, 0.05);

	CREDITO.put(tipos.ORO, 1000.0);
	CREDITO.put(tipos.PLATINO, 800.0);
	CREDITO.put(tipos.CLASICA, 600.0);
	CREDITO.put(tipos.OTRA, 600.0);
    }

    private double mCredito;
    private String mNombreEntidad;
    private int mCCV;
    private marcas mMarcaInternacional;
    private tipos mTipo;
    private Vector<Movimiento> mMovimientosCredito;

    public Credito(String numero, String titular, LocalDate fechaCaducidad) {
	super(numero, titular, fechaCaducidad);
	this.mMovimientosCredito = new Vector<Movimiento>();
    }

    public static BuilderCredito builder(String numero, String titular, LocalDate fechaCaducidad) {
	return new BuilderCredito(numero, titular, fechaCaducidad);
    }

    public void retirar(double x) throws Exception {
	double comisiontarifa = TARIFA.get(this.mMarcaInternacional);
	double comision = (x * comisiontarifa < MINCOMISION ? MINCOMISION : x * comisiontarifa);
	if (x > getCreditoDisponible())
	    throw new Exception("Crédito insuficiente");
	this.mMovimientosCredito
		.addElement(new Movimiento("Retirada en cuenta asociada (cajero automático)", x + comision));
    }

    public void ingresar(double x) throws Exception {
	double comision = (x * BASECOMISION < MINCOMISION ? MINCOMISION : x * BASECOMISION);
	if (x > getCreditoDisponible())
	    throw new Exception("Crédito insuficiente");
	this.mMovimientosCredito.addElement(new Movimiento("Traspaso desde tarjeta a cuenta", x));
	this.mCuentaAsociada.ingresar("Traspaso desde tarjeta a cuenta", x);
	this.mCuentaAsociada.retirar("Comision Traspaso desde tarjeta a cuenta", comision);
    }

    public void pagoEnEstablecimiento(String datos, double x) throws Exception {
	this.mMovimientosCredito.addElement(new Movimiento("Compra a crédito en: " + datos, x));
    }

    public double getSaldo() {
	double r = 0.0;
	for (int i = 0; i < this.mMovimientosCredito.size(); i++) {
	    Movimiento m = (Movimiento) mMovimientosCredito.elementAt(i);
	    r += m.getImporte();
	}
	return r;
    }

    public double getCreditoDisponible() {
	return this.mCredito - getSaldo();
    }

    public void liquidar(int mes, int anyo) throws Exception {
	double r = obtenerLiquidacion(mes, anyo);
	if (r != 0) {
	    this.mCuentaAsociada
		    .realizarMovimiento("Liquidación de operaciones tarj. crédito, " + (mes) + " de " + (anyo), -r);
	}
    }
    
    public double obtenerLiquidacion(int mes, int anyo) throws Exception {
	double r = 0.0;
	for (int i = 0; i < this.mMovimientosCredito.size(); i++) {
	    Movimiento m = (Movimiento) mMovimientosCredito.elementAt(i);
	    if (m.getFecha().getMonthValue() == mes && m.getFecha().getYear() == anyo && !m.isLiquidado())
		r += m.getImporte();
	    m.setLiquidado(true);
	}
	return r;
    }

    // liquidación parcial sobre el total de los gastos realizados con esa tarjeta
    // durante el mes/año de liquidación que consiste en lo siguiente:
    // los gastos totales, incluida una comisión de 12%, se dividen en 3 cuotas a
    // pagar en los 3 meses siguientes
    public void liquidarPlazos(int mes, int anyo) throws Exception {
	// TODO
    }

    public static class BuilderCredito {

	private Credito tarjetaCredito;

	public BuilderCredito(String numero, String titular, LocalDate fechaCaducidad) {
	    this.tarjetaCredito = new Credito(numero, titular, fechaCaducidad);
	}

	public BuilderCredito mCredito(double credito) {
	    this.tarjetaCredito.mCredito = credito;
	    return this;
	}

	public BuilderCredito mNombreEntidad(String nombreentidad) {
	    this.tarjetaCredito.mNombreEntidad = nombreentidad;
	    return this;
	}

	public BuilderCredito mCCV(int ccv) {
	    this.tarjetaCredito.mCCV = ccv;
	    return this;
	}

	public BuilderCredito mMarcaInternacional(marcas marcainternacional) {
	    this.tarjetaCredito.mMarcaInternacional = marcainternacional;
	    return this;
	}

	public BuilderCredito mTipo(tipos tipo) {
	    this.tarjetaCredito.mTipo = tipo;
	    this.mCredito(CREDITO.get(tipo));
	    return this;
	}

	public Credito build() {
	    return this.tarjetaCredito;
	}
    }

}