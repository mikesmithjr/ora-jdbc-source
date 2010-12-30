package oracle.jdbc.rowset;

import javax.sql.RowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

class OracleWebRowSetXmlReaderDomHandler extends OracleWebRowSetXmlReaderContHandler {
    OracleWebRowSetXmlReaderDomHandler(RowSet rowset) {
        super(rowset);
    }

    void readXMLDocument(Document doc) throws SAXException {
        Element wrsetElem = doc.getDocumentElement();
        startElement(null, null, "webRowSet", null);

        Node propElem = wrsetElem.getElementsByTagName("properties").item(0);

        startElement(null, null, "properties", null);

        NodeList propNodeList = propElem.getChildNodes();
        int propNodeNum = propNodeList.getLength();

        for (int i = 0; i < propNodeNum; i++) {
            Node propNode = propNodeList.item(i);

            if ((propNode instanceof Text)) {
                continue;
            }
            String propertyName = propNode.getNodeName();
            startElement(null, null, propertyName, null);

            if (propNode.hasChildNodes()) {
                processElement(propNode.getFirstChild().getNodeValue());
            } else {
                processElement("");
            }

            endElement(null, null, propertyName);
        }

        endElement(null, null, "properties");

        Node metaElem = wrsetElem.getElementsByTagName("metadata").item(0);

        startElement(null, null, "metadata", null);

        Node colCountElem = metaElem.getFirstChild().getNextSibling();

        String colCountElemName = colCountElem.getNodeName();
        startElement(null, null, colCountElemName, null);

        processElement(colCountElem.getFirstChild().getNodeValue());
        endElement(null, null, colCountElemName);

        NodeList colDefElems = metaElem.getChildNodes();
        int colDefNum = colDefElems.getLength();

        for (int i = 3; i < colDefNum; i++) {
            Node colDefElem = colDefElems.item(i);

            NodeList metaNodeList = colDefElem.getChildNodes();
            int metaNodeNum = metaNodeList.getLength();

            for (int j = 0; j < metaNodeNum; j++) {
                Node metaNode = metaNodeList.item(j);

                if ((metaNode instanceof Text)) {
                    continue;
                }
                String metaDataName = metaNode.getNodeName();
                startElement(null, null, metaDataName, null);

                if (metaNode.hasChildNodes()) {
                    processElement(metaNode.getFirstChild().getNodeValue());
                } else {
                    processElement("");
                }

                endElement(null, null, metaDataName);
            }
        }

        endElement(null, null, "metadata");

        Node dataElem = wrsetElem.getElementsByTagName("data").item(0);
        startElement(null, null, "data", null);

        NodeList rowNodeList = dataElem.getChildNodes();
        int rowNodeNum = rowNodeList.getLength();

        for (int i = 0; i < rowNodeNum; i++) {
            Node rowNode = rowNodeList.item(i);

            if ((rowNode instanceof Text)) {
                continue;
            }
            String rowElemName = rowNode.getNodeName();
            startElement(null, null, rowElemName, null);

            NodeList colNodeList = rowNode.getChildNodes();
            int colNodeNum = colNodeList.getLength();

            for (int j = 0; j < colNodeNum; j++) {
                Node colNode = colNodeList.item(j);

                if ((colNode instanceof Text)) {
                    continue;
                }
                String colElemName = colNode.getNodeName();
                startElement(null, null, colElemName, null);
                String colElemValue = null;
                if (colNode.hasChildNodes()) {
                    colElemValue = colNode.getFirstChild().getNodeValue();
                    if (colElemValue == null) {
                        startElement(null, null, "null", null);
                    }

                } else {
                    colElemValue = "";
                }

                processElement(colElemValue);

                endElement(null, null, colElemName);
            }

            endElement(null, null, rowElemName);
        }

        endElement(null, null, "data");

        endElement(null, null, "webRowSet");

        endDocument();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleWebRowSetXmlReaderDomHandler JD-Core Version: 0.6.0
 */