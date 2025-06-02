package bll;

import dao.PublisherDAO;
import Model.Publisher;
import java.util.List;

public class PublisherBLL {
    private PublisherDAO publisherDAO;

    public PublisherBLL() {
        this.publisherDAO = new PublisherDAO();
    }

    public List<Publisher> getAllPublishers() {
        return publisherDAO.getAllPublishers();
    }

    public Publisher getPublisherById(int publisherId) {
        return publisherDAO.getPublisherById(publisherId);
    }

    public boolean addPublisher(Publisher publisher) {
        return publisherDAO.addPublisher(publisher);
    }

    public boolean updatePublisher(Publisher publisher) {
        return publisherDAO.updatePublisher(publisher);
    }
}