package etsisi.ems.trabajo3.banco;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Movimiento {
    
    private String mConcepto;
    private LocalDate mFecha;
    private double mImporte;
    private boolean mLiquidado;

    public Movimiento() {
	setLiquidado(false); // lo necesito para los movimientos de las tarjetas de crédito
    }

    public Movimiento(String concepto, double importe) {
	this.mConcepto = concepto;
	this.mImporte = importe;
	this.mFecha = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	setLiquidado(false);
    }

    public double getImporte() {
	return this.mImporte;
    }

    public String getConcepto() {
	return this.mConcepto;
    }

    public void setConcepto(String newMConcepto) {
	this.mConcepto = newMConcepto;
    }

    public LocalDate getFecha() {
	return this.mFecha;
    }

    public void setFecha(LocalDate newMFecha) {
	this.mFecha = newMFecha;
    }

    public void setImporte(double newMImporte) {
	this.mImporte = newMImporte;
    }

    public boolean isLiquidado() {
	return this.mLiquidado;
    }

    public void setLiquidado(boolean mliquidado) {
	this.mLiquidado = mliquidado;
    }

}