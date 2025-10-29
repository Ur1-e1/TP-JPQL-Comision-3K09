package org.example.managers;

import org.example.entidades.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
//Integrantes del grupo:
//-Daniel Salomón
//-Luciano Cordoba
//-Italo Rocamora
//-Uriel Romero
public class MainConsultasJPQL {

    public static void main(String[] args) {

        listarTodosLosClientes();

        listarFacturasUltimoMes();

        obtenerClienteConMasFacturas();

        listarArticulosMasVendidos();

        consultarFacturasUltimosTresMesesDeCliente(1L);


       calcularMontoTotalFacturadoPorCliente(1L);
        listarArticulosVendidosEnFactura(1L);
        obtenerArticuloMasCaroEnFactura(1L);
        contarTotalFacturasGeneradas();
        listarFacturasConTotalMayorA(100.0);


        consultarFacturasPorNombreArticulo("Manzana");
        listarArticulosPorCodigoParcial("17616962");
        listarArticulosConPrecioMayorAlPromedio();
        listarClientesConFacturas();

    }



    // Ejercicio 1
    public static void listarTodosLosClientes() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT c FROM Cliente c";
            Query query = em.createQuery(jpql);
            List<Cliente> clientes = query.getResultList();
            System.out.println("Todos los clientes:");
            clientes.forEach(cliente -> System.out.println(cliente.getRazonSocial()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 2
    public static void listarFacturasUltimoMes() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            LocalDate fechaLimite = LocalDate.now().minusMonths(1);
            String jpql = "SELECT f FROM Factura f WHERE f.fechaComprobante >= :fechaLimite";
            Query query = em.createQuery(jpql);
            query.setParameter("fechaLimite", fechaLimite);
            List<Factura> facturas = query.getResultList();
            System.out.println("Facturas del ultimo mes:");
            facturas.forEach(factura -> System.out.println("Nro: " + factura.getNroComprobante() + ", Fecha: " + factura.getFechaComprobante()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 3
    public static void obtenerClienteConMasFacturas() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT c, COUNT(f) FROM Cliente c LEFT JOIN c.facturas f GROUP BY c ORDER BY COUNT(f) DESC";
            Query query = em.createQuery(jpql);
            query.setMaxResults(1);
            Object[] resultado = (Object[]) query.getSingleResult();
            Cliente cliente = (Cliente) resultado[0];
            Long totalFacturas = (Long) resultado[1];
            System.out.println("Cliente con mas facturas:");
            System.out.println("Cliente: " + cliente.getRazonSocial() + ", Total de facturas: " + totalFacturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 4
    public static void listarArticulosMasVendidos() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT a, SUM(fd.cantidad) FROM FacturaDetalle fd JOIN fd.articulo a GROUP BY a ORDER BY SUM(fd.cantidad) DESC";
            Query query = em.createQuery(jpql);
            List<Object[]> resultados = query.getResultList();
            System.out.println("Articulos mas vendidos:");
            resultados.forEach(result -> {
                Articulo articulo = (Articulo) result[0];
                Double totalVendido = (Double) result[1];
                System.out.println("Articulo: " + articulo.getDenominacion() + ", Total vendido: " + totalVendido);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 5
    public static void consultarFacturasUltimosTresMesesDeCliente(Long clienteId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            LocalDate fechaLimite = LocalDate.now().minusMonths(3);
            String jpql = "SELECT f FROM Factura f WHERE f.cliente.id = :clienteId AND f.fechaComprobante >= :fechaLimite";
            Query query = em.createQuery(jpql);
            query.setParameter("clienteId", clienteId);
            query.setParameter("fechaLimite", fechaLimite);
            List<Factura> facturas = query.getResultList();
            System.out.println("Facturas de los ultimos 3 meses para el cliente ID " + clienteId + " :");
            facturas.forEach(factura -> System.out.println("Nro: " + factura.getNroComprobante() + ", Fecha: " + factura.getFechaComprobante()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 6
    public static void calcularMontoTotalFacturadoPorCliente(Long clienteId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT SUM(f.total) FROM Factura f WHERE f.cliente.id = :clienteId";
            Query query = em.createQuery(jpql);
            query.setParameter("clienteId", clienteId);
            Double montoTotal = (Double) query.getSingleResult();
            System.out.println("Monto total facturado por el cliente ID " + clienteId + " :");
            System.out.println("Monto total: " + (montoTotal != null ? montoTotal : 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 7
    public static void listarArticulosVendidosEnFactura(Long facturaId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT fd.articulo FROM FacturaDetalle fd WHERE fd.factura.id = :facturaId";
            Query query = em.createQuery(jpql);
            query.setParameter("facturaId", facturaId);
            List<Articulo> articulos = query.getResultList();
            System.out.println("Articulos vendidos en la factura ID " + facturaId + " :");
            articulos.forEach(articulo -> System.out.println("Articulo: " + articulo.getDenominacion() + ", Precio: " + articulo.getPrecioVenta()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 8
    public static void obtenerArticuloMasCaroEnFactura(Long facturaId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT a FROM FacturaDetalle fd JOIN fd.articulo a WHERE fd.factura.id = :facturaId ORDER BY a.precioVenta DESC";
            Query query = em.createQuery(jpql);
            query.setParameter("facturaId", facturaId);
            query.setMaxResults(1);
            Articulo articulo = (Articulo) query.getSingleResult();
            System.out.println("Articulo mas caro en la factura ID " + facturaId + " :");
            System.out.println("Articulo: " + articulo.getDenominacion() + ", Precio: " + articulo.getPrecioVenta());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 9
    public static void contarTotalFacturasGeneradas() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(f) FROM Factura f";
            Query query = em.createQuery(jpql);
            Long totalFacturas = (Long) query.getSingleResult();
            System.out.println("Total de facturas generadas en el sistema:");
            System.out.println("Total: " + totalFacturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 10
    public static void listarFacturasConTotalMayorA(Double valorMinimo) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT f FROM Factura f WHERE f.total > :valorMinimo";
            Query query = em.createQuery(jpql);
            query.setParameter("valorMinimo", valorMinimo);
            List<Factura> facturas = query.getResultList();
            System.out.println("Facturas con total mayor a " + valorMinimo + " :");
            facturas.forEach(factura -> System.out.println("Nro: " + factura.getNroComprobante() + ", Total: " + factura.getTotal()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }




    }

    // Ejercicio 11
    public static void consultarFacturasPorNombreArticulo(String nombreArticulo) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT f FROM Factura f JOIN f.detallesFactura fd JOIN fd.articulo a WHERE a.denominacion = :nombreArticulo";
            Query query = em.createQuery(jpql);
            query.setParameter("nombreArticulo", nombreArticulo);
            List<Factura> facturas = query.getResultList();
            System.out.println("Facturas que contienen el articulo " + nombreArticulo + " :");
            facturas.forEach(factura -> System.out.println("Nro: " + factura.getNroComprobante() + ", Fecha: " + factura.getFechaComprobante()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 12
    public static void listarArticulosPorCodigoParcial(String codigoParcial) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT a FROM Articulo a WHERE a.codigo LIKE :codigoParcial";
            Query query = em.createQuery(jpql);
            query.setParameter("codigoParcial", "%" + codigoParcial + "%");
            List<Articulo> articulos = query.getResultList();
            System.out.println("Articulos con codigo parcial " + codigoParcial + " :");
            articulos.forEach(articulo -> System.out.println("Codigo: " + articulo.getCodigo() + ", Denominacion: " + articulo.getDenominacion()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 13
    public static void listarArticulosConPrecioMayorAlPromedio() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT a FROM Articulo a WHERE a.precioVenta > (SELECT AVG(a2.precioVenta) FROM Articulo a2)";
            Query query = em.createQuery(jpql);
            List<Articulo> articulos = query.getResultList();
            System.out.println("Articulos con precio mayor al promedio:");
            articulos.forEach(articulo -> System.out.println("Denominacion: " + articulo.getDenominacion() + ", Precio: " + articulo.getPrecioVenta()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Ejercicio 14
    public static void listarClientesConFacturas() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT c FROM Cliente c WHERE EXISTS (" +
                    "SELECT 1 FROM Factura f WHERE f.cliente = c)";
            Query query = em.createQuery(jpql);
            List<Cliente> clientes = query.getResultList();
            System.out.println("Clientes que tienen al menos una factura:");
            clientes.forEach(cliente -> System.out.println("ID: " + cliente.getId() + ", Razon Social: " + cliente.getRazonSocial()));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }


}
