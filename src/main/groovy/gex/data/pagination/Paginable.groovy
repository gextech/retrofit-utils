package gex.data.pagination

/**
 * Created by Tsunllly on 3/6/15.
 */
trait Paginable {

  
  static Map searchPage(PageParams pageParams) {
    searchPage(pageParams){}
  }

  static Map searchPage(PageParams pageParams, Closure finder) {

    if(!pageParams) {
      pageParams = new PageParams()
    }

    Class domainClass = Class.forName(this.name)

    Integer from = (pageParams.from == null) ? 0 : pageParams.from
    Integer size = (pageParams.size == null) ? 10 : pageParams.size

    pageParams = new PageParams(from: from, size: size)

    List elements = PaginableHelper.executeSearch(domainClass, pageParams, false, finder)
    Long total = PaginableHelper.executeSearch(domainClass, pageParams, true, finder).first()

    [
      domainClazz : domainClass,
      items: elements,
      page : [
        pageTotal: elements.size(),
      ] + PaginableHelper.getPageCounts(total, from, size)
    ]
  }


}