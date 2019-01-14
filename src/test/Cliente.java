package test;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.autoabm.anotaciones.CampoABM;
import com.autoabm.anotaciones.IdABM;

@Entity
public class Cliente {
	@Id
	@GeneratedValue
	@IdABM
	private int id;
	@CampoABM(validar=true)
	private String nombre;
	@CampoABM(validar=true)
	private String apellido;
	@CampoABM(validar=true)
	@Column(unique=true)
	private String documento;
	@CampoABM(datos={"MASCULINO","FEMENINO"})
	private String sexo;
	@CampoABM(validar=true,nombre="F. Nacimiento")
	private Date fechaNacimiento;
	@CampoABM(validar=true,nombre="Crédito")
	private Double credito;
	public Double getCredito() {
		return credito;
	}
	public void setCredito(Double credito) {
		this.credito = credito;
	}
	@CampoABM(nombre="Obs",textoLargo=true)
	private String observaccion;
	@CampoABM
	private Boolean estado;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public Boolean getEstado() {
		return estado;
	}
	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	public String getApellido() {
		return apellido;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getObservaccion() {
		return observaccion;
	}
	public void setObservaccion(String observaccion) {
		this.observaccion = observaccion;
	}
	@Override
	public String toString() {
		return "Cliente [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", sexo=" + sexo
				+ ", fechaNacimiento=" + fechaNacimiento + ", credito=" + credito + ", observaccion=" + observaccion
				+ ", estado=" + estado + "]";
	}
	
	
	
}
