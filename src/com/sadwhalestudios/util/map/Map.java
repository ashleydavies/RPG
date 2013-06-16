package com.sadwhalestudios.util.map;

import com.sadwhalestudios.util.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author
 * Ashley
 */
public class Map {
    String name;
    Document info;
    int width;
    int height;
    MapLayer layers[];
    
    public void render()
    {
        
    }
    
    public void load()
    {
        info = XMLParser.instance.parseXML(this.getClass().getClassLoader().getResourceAsStream("data/xml/map/1.xml"));
        
        name = info.getElementsByTagName("name").item(0).getTextContent();
        width = Integer.parseInt(info.getElementsByTagName("mapWidth").item(0).getTextContent());
        height = Integer.parseInt(info.getElementsByTagName("mapHeight").item(0).getTextContent());
        
        Element layerRoot = (Element)info.getElementsByTagName("tileData").item(0);
        NodeList layerNodes = layerRoot.getElementsByTagName("layer");
        
        layers = new MapLayer[layerNodes.getLength()];
        
        for (int i = 0; i < layerNodes.getLength(); i++)
        {
            Element i_layerNode = (Element)layerNodes.item(i);
            
            int i_layerNodeID = Integer.parseInt(i_layerNode.getAttribute("id"));
            
            NodeList i_rowNodes = i_layerNode.getElementsByTagName("row");
            MapTileData[][] i_layerTiles = new MapTileData[height][width];
            
            for (int r = 0; r < i_rowNodes.getLength(); r++)
            {
                Element i_rowNode = (Element)i_rowNodes.item(r);
                
                int i_rowNodeID = Integer.parseInt(i_rowNode.getAttribute("id"));
                
                NodeList i_colNodes = i_rowNode.getElementsByTagName("col");
                i_layerTiles[i_rowNodeID] = new MapTileData[width];
                
                for (int c = 0; c < i_colNodes.getLength(); c++)
                {
                    Element i_colNode = (Element)i_colNodes.item(c);
                    
                    int i_colNodeID = Integer.parseInt(i_colNode.getAttribute("id"));
                    
                    Element i_tile = (Element)i_colNode.getElementsByTagName("tile").item(0);
                    
                    int i_tileID = Integer.parseInt(i_tile.getAttribute("tileID"));
                    
                    i_layerTiles[i_rowNodeID][i_colNodeID] = new MapTileData(i_tileID);
                }
            }
            
            layers[i_layerNodeID] = new MapLayer(i_layerTiles);
        }
    }
}
