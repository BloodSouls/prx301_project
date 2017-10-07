package dao;

import entities.Book;
import entities.Genre;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import ultis.Ultilities;

public class BookDAO {

  public static Book getBook(int bookId) {
    EntityManager em = Ultilities.getEntityManager();
    Book book = null;

    try {
      TypedQuery<Book> query = em.createNamedQuery("Book.findById", Book.class);
      query.setParameter("id", bookId);
      book = query.getSingleResult();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }

    return book;
  }
  
  public static Book getBookAndIncreaseView(int bookId) {
    EntityManager em = Ultilities.getEntityManager();
    Book book = null;

    try {
      TypedQuery<Book> query = em.createNamedQuery("Book.findById", Book.class);
      query.setParameter("id", bookId);
      book = query.getSingleResult();
      
      em.getTransaction().begin();
      book.setTotalView(book.getTotalView() + 1);
      em.getTransaction().commit();
    } catch (Exception e) {
      em.getTransaction().rollback();
      e.printStackTrace();
    } finally {
      em.close();
    }

    return book;
  }

  public static int createBook(Book book) {
    EntityManager em = Ultilities.getEntityManager();

    try {
      em.getTransaction().begin();
      em.persist(book);
      em.flush();
      em.getTransaction().commit();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }

    return book.getId();
  }

  public static List<Book> searchBookByName(String name, int quantity, int pageNum) {
    if (pageNum < 0) {
      pageNum = 0;
    }
    if (quantity < 0) {
      quantity = 0;
    }
    EntityManager em = Ultilities.getEntityManager();
    List<Book> books = null;

    try {
      TypedQuery<Book> query = em.createQuery(
              "SELECT b FROM Book b WHERE b.name LIKE :name",
              Book.class)
              .setFirstResult(pageNum * quantity)
              .setMaxResults(quantity);
      query.setParameter("name", "%" + name + "%");
      books = query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return books;
  }
  
  public static List<Book> searchBookByNameAndGenreList(String name,
          List<Integer> genreList, int quantity, int pageNum) {
    EntityManager em = Ultilities.getEntityManager();
    List<Book> result = null;
    
    try {
      TypedQuery<Book> query = em.createQuery(
              "SELECT DISTINCT m.bookId"
              + " FROM BookGenreMapping m"
              + " WHERE m.genreId IN :genreList"
                + " AND m.bookId.name LIKE :name",
              Book.class)
              .setFirstResult(quantity * pageNum)
              .setMaxResults(quantity);
      query.setParameter("name", "%" + name + "%");
      query.setParameter("genreList", genreList);
      
      result = query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }
    
    return result;
  }

  public static List<Book> getMostViewedBooks(int quantity, int pageNum) { // first page is 0
    if (pageNum < 0) {
      pageNum = 0;
    }
    if (quantity < 0) {
      quantity = 0;
    }
    EntityManager em = Ultilities.getEntityManager();
    List<Book> result = null;

    try {
      TypedQuery<Book> query = em.createQuery(
              "SELECT b FROM Book b ORDER BY b.totalView DESC", Book.class)
              .setFirstResult(pageNum * quantity)
              .setMaxResults(quantity);
      result = query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }

    return result;
  }
  
  public static List<Book> getBookByGenreIds(List<Integer> idList,
          int bookQuantity, int pageNum) {
    EntityManager em = Ultilities.getEntityManager();
    List<Book> result = null;
    
    try {
      TypedQuery<Book> query = em.createQuery(
              "SELECT DISTINCT m.bookId"
              + " FROM BookGenreMapping m"
              + " WHERE m.genreId IN :idList",
              Book.class)
              .setFirstResult(pageNum * bookQuantity)
              .setMaxResults(bookQuantity);
      query.setParameter("idList", idList);
      
      result = query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return result;
  }

  public static List<Book> getNewBook(int quantity, int pageNum) {
    if (pageNum < 0) {
      pageNum = 0;
    }
    if (quantity < 0) {
      quantity = 0;
    }
    EntityManager em = Ultilities.getEntityManager();
    List<Book> result = null;

    try {
      TypedQuery<Book> query = em.createQuery(
              "SELECT b FROM Book b ORDER BY b.creatingDate DESC", Book.class)
              .setFirstResult(pageNum * quantity)
              .setMaxResults(quantity);

      result = query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }

    return result;
  }

  public static List<Book> getUpdatedBooks(int quantity, int pageNum) {
    if (pageNum < 0) {
      pageNum = 0;
    }
    if (quantity < 0) {
      quantity = 0;
    }
    EntityManager em = Ultilities.getEntityManager();
    List<Book> result = null;

    try {

      TypedQuery<Object[]> query = em.createQuery(
              "SELECT DISTINCT c.bookId, MAX(c.releasedDate)"
              + " FROM Chapter c"
              + " GROUP BY c.bookId"
              + " ORDER BY MAX(c.releasedDate) DESC", Object[].class)
              .setFirstResult(pageNum * quantity)
              .setMaxResults(quantity);

      List<Object[]> list = query.getResultList();
      result = new ArrayList<Book>();
      for (Object[] obj : list) {
        result.add((Book) obj[0]);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }

    return result;
  }

}
