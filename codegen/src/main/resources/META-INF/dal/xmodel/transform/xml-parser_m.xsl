<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>

<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
   <xsl:value-of select="$empty"/>#import "XmlParser.h"<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name="parser-implementation"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name="parser-delegate-implementation"/>
</xsl:template>

<xsl:template name="parser-implementation">
   <xsl:value-of select="$empty"/>@implementation <xsl:value-of select="@xml-parser-class"/> : NSObject<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>+ (<xsl:value-of select="/model/entity[@root='true']/@class-name"/> *)parseXml:(NSData *)xml {<xsl:value-of select="$empty"/>
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:xml];
    <xsl:value-of select="@xml-parser-delegate"/> *delegate = [[<xsl:value-of select="@xml-parser-delegate"/> alloc] init];

    [parser setDelegate:delegate];
    [parser setShouldProcessNamespaces:NO];
    [parser setShouldReportNamespacePrefixes:NO];
    [parser setShouldResolveExternalEntities:NO];
    [parser parse];

    NSError *err = [parser parserError];

    if (err) {
        NSLog(@"XML parsing error(%d): %@!", [err code], [err localizedDescription]);
    }

    [err release];
    [parser release];

    return delegate._<xsl:value-of select="/model/entity[@root='true']/@param-name"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@end<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="parser-delegate-implementation">
   <xsl:value-of select="$empty"/>@implementation <xsl:value-of select="@xml-parser-delegate"/> {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>    NSMutableArray *m_objs;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>    NSMutableArray *m_tags;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>    NSMutableArray *m_text;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@synthesize _<xsl:value-of select="/model/entity[@root='true']/@param-name"/>;<xsl:value-of select="$empty-line"/>
   
   <xsl:call-template name="init-dealloc-method"/>
   <xsl:call-template name="xml-parser-method"/>
   
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>- (<xsl:value-of select="@class-name"/> *)<xsl:value-of select="@build-method"/>:(NSDictionary *)attrs {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="'    '"/><xsl:value-of select="@class-name"/> *<xsl:value-of select="@param-name"/> = [[<xsl:value-of select="@class-name"/> alloc] init];<xsl:value-of select="$empty-line"/>

      <xsl:for-each select="attribute[not(@render='false')]">
         <xsl:value-of select="$empty"/>    NSString *<xsl:value-of select="@param-name"/> = [attrs valueForKey:@"<xsl:value-of select="@name"/>"];<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      
      <xsl:for-each select="attribute[not(@render='false')]">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>    if (<xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>       <xsl:value-of select="'       '"/><xsl:value-of select="../@param-name"/>.<xsl:value-of select="@property"/> = <xsl:call-template name="from-string-value"/>;<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    return <xsl:value-of select="@param-name"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>- (void)parse:(NSString*)name attributes:(NSDictionary *)attrs {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    if ([name isEqualToString:@"<xsl:value-of select="/model/entity[@root='true']/@tag-name"/>"]) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>        self._<xsl:value-of select="/model/entity[@root='true']/@param-name"/> = [self <xsl:value-of select="/model/entity[@root='true']/@build-method"/>:attrs];<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>        [m_objs addObject:self._<xsl:value-of select="/model/entity[@root='true']/@param-name"/>];<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>        [m_tags addObject:name];<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    } else {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>        @throw([NSException exceptionWithName:@"RuntimeException" reason:@"Root element(<xsl:value-of select="/model/entity[@root='true']/@tag-name"/>) expected!" userInfo:nil]);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
   
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>- (void)<xsl:value-of select="@parse-for-method"/>:(<xsl:value-of select="@class-name"/> *)parentObj parentTag:(NSString*)parentTag name:(NSString*)name attributes:(NSDictionary *)attrs {<xsl:value-of select="$empty-line"/>
      <xsl:variable name="refs" select="entity-ref[(@list='true' or @map='true') and not(@render='false')]"/>
      <xsl:choose>
         <xsl:when test="$refs">
            <xsl:variable name="indent-refs" select="entity-ref[(@list='true' or @map='true') and @xml-indent='true' and not(@render='false')] | element[not(@text='true') and not(@render='false')]"/>
            <xsl:if test="$indent-refs">
               <xsl:value-of select="$empty"/>    if (<xsl:value-of select="$empty"/>
               <xsl:for-each select="entity-ref[(@list='true' or @map='true') and @xml-indent='true' and not(@render='false')] | element[not(@text='true') and not(@render='false')]">
                  <xsl:value-of select="$empty"/>[name isEqualToString:@"<xsl:value-of select="@tag-name"/>"]<xsl:value-of select="$empty"/>
                  <xsl:if test="name()='element' and (@list='true' or @set='true')">
                     <xsl:value-of select="$empty"/> || [name isEqualToString:@"<xsl:value-of select="@name"/>"]<xsl:value-of select="$empty"/>
                  </xsl:if>
                  <xsl:if test="position() != last()"> || </xsl:if>
               </xsl:for-each>
               <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>       [m_objs addObject:parentObj];<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>    } else <xsl:value-of select="$empty"/>
            </xsl:if>
            <xsl:for-each select="$refs">
               <xsl:variable name="name" select="@name"/>
               <xsl:variable name="entity" select="/model/entity[@name=$name]"/>
               <xsl:choose>
                  <xsl:when test="position()=1 and $indent-refs"></xsl:when>
                  <xsl:when test="position()=1"><xsl:value-of select="'    '"/></xsl:when>
                  <xsl:otherwise> else </xsl:otherwise>
               </xsl:choose>
               <xsl:value-of select="$empty"/>if ([name isEqualToString:@"<xsl:value-of select="@name"/>"]) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="'       '"/><xsl:value-of select="$entity/@class-name"/> *<xsl:value-of select="$entity/@param-name"/> = [self <xsl:value-of select="$entity/@build-method"/>:attrs];<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>       [parentObj <xsl:value-of select="@add-method"/>:<xsl:value-of select="$entity/@param-name"/>];<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>       [m_objs addObject:<xsl:value-of select="$entity/@param-name"/>];<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty"/>
            </xsl:for-each>
            <xsl:value-of select="$empty"/> else {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>       @throw([NSException exceptionWithName:@"RuntimeException" reason:[NSString stringWithFormat:@"Element(%@) is not expected under <xsl:value-of select="../@tag-name"/>!", name] userInfo:nil]);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>    [m_objs addObject:parentObj];<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
    
      <xsl:value-of select="$empty"/>    [m_tags addObject:name];<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
   </xsl:for-each>

   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@end<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="xml-parser-method">
- (void)parserDidStartDocument:(NSXMLParser *)parser {
}

- (void)parserDidEndDocument:(NSXMLParser *)parser {
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)name namespaceURI:(NSString *)uri qualifiedName:(NSString *)qName attributes:(NSDictionary *)attrs {
    if (uri == nil) {
        if ([m_objs count] == 0) {
            [self parse:name attributes:attrs];
        } else {
            NSObject *parent = [m_objs lastObject];
            NSString *tag = [m_tags lastObject];<xsl:value-of select="$empty-line"/>

            <xsl:value-of select="$empty-line"/>
            <xsl:for-each select="entity">
               <xsl:choose>
                  <xsl:when test="position()=1"><xsl:value-of select="'            '"/></xsl:when>
                  <xsl:otherwise> else </xsl:otherwise>
               </xsl:choose>
               
               <xsl:value-of select="$empty"/>if ([parent isKindOfClass:[<xsl:value-of select="@class-name"/> class]]) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>                [self <xsl:value-of select="@parse-for-method"/>:(<xsl:value-of select="@class-name"/> *)parent parentTag:tag name:name attributes:attrs];<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>             }<xsl:value-of select="$empty"/>
            </xsl:for-each>
            
            <xsl:value-of select="$empty"/> else {
                @throw([NSException exceptionWithName:@"RuntimeException" reason:[NSString stringWithFormat:@"Unknown entity(%@) under %@!", name, [parent class]] userInfo:nil]);
            }
        }
        
        [m_text removeAllObjects];
    } else {
        @throw([NSException exceptionWithName:@"RuntimeException" reason:[NSString stringWithFormat:@"Namespace(%@) is not supported by %@!", uri, [self class]] userInfo:nil]);
    }
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)name namespaceURI:(NSString *)uri qualifiedName:(NSString *)qName {
    if (uri == nil) {<xsl:value-of select="$empty"/>
        <xsl:if test="/model/entity[element[not(@render='false')]]">
           <xsl:variable name="indent" select="'        '"/>
           <xsl:value-of select="$empty-line"/>
           <xsl:value-of select="$indent"/>id currentObj = [m_objs lastObject];<xsl:value-of select="$empty-line"/>
           <xsl:value-of select="$indent"/>NSString *currentTag = [m_tags lastObject];<xsl:value-of select="$empty-line"/>
           <xsl:value-of select="$empty-line"/>
           <xsl:for-each select="/model/entity[element[not(@render='false')]]">
               <xsl:choose>
                  <xsl:when test="position()=1"><xsl:value-of select="$indent"/></xsl:when>
                  <xsl:otherwise> else </xsl:otherwise>
               </xsl:choose>
               <xsl:value-of select="$empty"/>if ([currentObj isKindOfClass:[<xsl:value-of select="@class-name"/> class]]) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$indent"/>   <xsl:value-of select="'   '"/><xsl:value-of select="@class-name"/> *<xsl:value-of select="@param-name"/>  = currentObj;<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:for-each select="element[not(@render='false')]">
                  <xsl:choose>
                     <xsl:when test="position()=1"><xsl:value-of select="$indent"/><xsl:value-of select="'   '"/></xsl:when>
                     <xsl:otherwise> else </xsl:otherwise>
                  </xsl:choose>
                  <xsl:value-of select="$empty"/>if ([currentTag isEqualToString:@"<xsl:value-of select="@name"/>"]) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      <xsl:value-of select="'      '"/><xsl:value-of select="../@param-name"/>.<xsl:value-of select="@name"/> = [m_text componentsJoinedByString:@""];<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty"/>
               </xsl:for-each>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty"/>
           </xsl:for-each>
           <xsl:value-of select="$empty-line"/>
        </xsl:if>
        [m_objs removeLastObject];
        [m_tags removeLastObject];
    }
    
    [m_text removeAllObjects];
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)data {
    [m_text addObject:data]; 
}
</xsl:template>

<xsl:template name="init-dealloc-method">
- (id)init {
    if (self = [super init]) {
       self-<xsl:call-template name="gt"/>m_objs = [[NSMutableArray alloc] init];
       self-<xsl:call-template name="gt"/>m_tags = [[NSMutableArray alloc] init];
       self-<xsl:call-template name="gt"/>m_text = [[NSMutableArray alloc] init];
    }
    
    return self;
}

- (void)dealloc {
    [m_objs release];
    [m_tags release];
    [m_text release];
    [_book release];
    
    [super dealloc];
}
</xsl:template>

<xsl:template name="from-string-value">
   <xsl:param name="entity" select=".."/>
   
   <xsl:choose>
      <xsl:when test="@primitive='true'">[<xsl:value-of select="@property"/> integerValue]</xsl:when>
      <xsl:when test="@value-type='NSNumber'">[<xsl:value-of select="$entity/@param-name"/> numberFromString:<xsl:value-of select="@property"/>]</xsl:when>
      <xsl:when test="@value-type='NSDate'">[<xsl:value-of select="$entity/@param-name"/> dateFromString:<xsl:value-of select="@property"/> format:@"<xsl:value-of select="@format"/>"]</xsl:when>
      <xsl:otherwise><xsl:value-of select="@property"/></xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
